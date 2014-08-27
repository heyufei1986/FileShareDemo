package smb;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SmbDemoLanucher
{
	public static void main(String[] args)
	{
		try{
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("domain", "QQBrowser", "tencent.mtt");
			SmbFile smbFile = new SmbFile("smb://10.7.11.232/QQBrowser/users/feihe/poc2.html", auth);
			
			System.out.println("smbfile.isDirectory():" + smbFile.isDirectory());
			
			int length = smbFile.getContentLength();
			byte buffer[] = new byte[length];
			SmbFileInputStream in = new SmbFileInputStream(smbFile);
			while(in.read(buffer) != -1){
				System.out.write(buffer);
				System.out.println(buffer.length);
			}
			
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
