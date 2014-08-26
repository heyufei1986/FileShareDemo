-module(session_registry).
-behavior(gen_server).
%% gen_server callbacks
-export([init/1, handle_call/3, handle_cast/2, handle_info/2, terminate/2,
         code_change/3]).
-export([start_link/0, lookup/1, remove/1, remove_self/1, add/2,
         get_as_list/0]).

-import(json_eep, [json_to_term/1, term_to_json/1, get_state/0]).

% TODO: implement a "clean/0" function that scans the table and removes stale entries


%% The external interface to other modules
-spec add(Prefix :: string(), Pid :: pid()) -> ok | already_exists | error.
add(Prefix, Pid) when is_list(Prefix), is_pid(Pid) ->
    log(debug, "session_registry:add for ~p, ~p~n", [Prefix, Pid]),
    gen_server:call(session_registry, {add, {Prefix, Pid}}).

% Sometimes we want to remove an entry from the session registry, but only the entry
% points to the calling process. This works around a race condition where a
% stale process will remove an entry to a newer process.
-spec remove_self(Prefix :: string()) -> ok.
remove_self(Prefix) when is_list(Prefix) ->
    Pid = self(),
    log(debug, "session_registry:remove_self for ~p from ~p~n", [Prefix, Pid]),
    gen_server:call(session_registry, {remove_if_pid, Prefix, Pid}).
    
-spec remove(Prefix :: string()) -> ok.
remove(Prefix) when is_list(Prefix) ->
    log(debug, "session_registry:remove for ~p~n", [Prefix]),
    gen_server:call(session_registry, {remove, Prefix}).

-spec lookup(Prefix :: string()) -> pid() | none. 
lookup(Prefix) when is_list(Prefix) ->
    log(debug, "session_registry:lookup for ~p~n", [Prefix]),
    gen_server:call(session_registry, {lookup, Prefix}).

get_as_list() ->
    gen_server:call(session_registry, {get_as_list}).

%% dump_state() ->
%%     gen_server:call(session_registry, {remove, Prefix}).
%%     wait_for_response().


start_link() ->
    gen_server:start_link({local, session_registry}, ?MODULE, [], []).

init([]) ->
    process_flag(trap_exit, true),
    {ok, ets:new(registry_ets, [set])}.  % Unordered, key must be unique         

handle_call({lookup, Prefix}, _From, State = EtsTable) ->
    {reply, 
     lookup_internal(EtsTable, Prefix), % Param has form string()
     State};
handle_call({add, Prefix}, _From, State = EtsTable) ->
    {reply, 
     add_internal(EtsTable, Prefix), % Param has form {Prefix, Pid}
     State};
handle_call({remove_if_pid, Prefix, Pid}, _From, State = EtsTable) ->
    remove_if_pid_internal(EtsTable, Prefix, Pid), % Param has form string()
    {reply,
     ok,
     State};
handle_call({remove, Prefix}, _From, State = EtsTable) ->
    remove_internal(EtsTable, Prefix), % Param has form string()
    {reply,
     ok,
     State};

% For debugging, return the current ETS table as a list.
handle_call({get_as_list}, _From, State = EtsTable) ->
    {reply,
     ets:tab2list(EtsTable),
     State}.


add_internal(EtsTable, Param = {Prefix, Pid}) when is_list(Prefix), is_pid(Pid) ->
    ets:insert(EtsTable, Param).

lookup_internal(EtsTable, Prefix) when is_list(Prefix) ->
    case ets:lookup(EtsTable, Prefix) of
        [{Prefix, Pid}]->
            Pid;
        _ ->
            none
    end.

remove_if_pid_internal(EtsTable, Prefix, Pid) when is_list(Prefix) ->
    case ets:lookup(EtsTable, Prefix) of
        [{Prefix, Pid}] ->
            log(debug, "Pid ~p matched, removing~n", [Pid]),
            ets:delete(EtsTable, Prefix);
        _ ->
            log(debug, "Pid ~p didn't match~n", [Pid]),
            ok
    end.

remove_internal(EtsTable, Prefix) ->
    ets:delete(EtsTable, Prefix).

terminate(Reason, _State) ->
    case Reason of
        normal -> ok;
        shutdown -> ok;
        {shutdown, _} -> ok;
        _ ->
            % If this thread dies,  kill all device sessions,
            % since all prefix-to-process mappings will be lost.
            % It would be nice to not kill processes that are in mid-transfer, but
            % that require some re-architecting.
            log(error, "Session registry terminated due to: ~p~n", [Reason]),
            log(error, "Restarting device session supervisor to reconnect devices~n", []),
            supervisor:restart_child(swiftp_proxy, device_session_sup),
            ok
    end.

code_change(_OldVsn, State, _Extra) ->
    {ok, State}.

handle_cast(_Msg, State) ->
    {noreply, State}.

handle_info(_Msg, State) ->
    {noreply, State}.

log(Level, Format, Args) ->
    log:log(Level, ?MODULE, Format, Args).
