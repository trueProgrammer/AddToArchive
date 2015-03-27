package com.exactprosystems.addtowar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main
{
	private static final String LIB_FOLDER = "libs\\";
	private static final String FOLDER = "\\WEB-INF\\lib\\";
	
	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.out.println("Incorrect paramateres. Zip Name is mandatory.");
			System.exit(0);
		}
		
		String fileName = args[0];
		
		System.out.println("START");
		
		ZipOutputStream out = null;
		ZipInputStream zin = null;
		
		try
		{
			
			File zipFile = new File(fileName);
			
			File tempFile = File.createTempFile(zipFile.getName(), null);
			tempFile.delete();
			
			boolean renameOk = zipFile.renameTo(tempFile);
			if (!renameOk)
			{
				throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
			}
			
			byte[] buf = new byte[1024];
			
			zin = new ZipInputStream(new FileInputStream(tempFile));
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			
			ZipEntry entry = null;
			
			while ((entry = zin.getNextEntry()) != null)
			{
				
				if (entry.getName().equals("jetty/webapps/sailfishcssuite.war"))
				{
					
					File file = File.createTempFile("war", null);
					
					FileOutputStream outWar = new FileOutputStream(file);
					
					int len;
					while ((len = zin.read(buf)) != -1)
					{
						outWar.write(buf, 0, len);
					}
					
					outWar.flush();
					outWar.close();
					
					updateWarFile(file);
					
					ZipInputStream zinWar = new ZipInputStream(new FileInputStream(file));
					
					out.putNextEntry(new ZipEntry(entry.getName()));
					
					FileInputStream fin = new FileInputStream(file);
					
					while ((len = fin.read(buf)) != -1)
					{
						out.write(buf, 0, len);
					}
					
					fin.close();
					zinWar.close();
					
					continue;
				}
				
				out.putNextEntry(new ZipEntry(entry.getName()));
				
				int len;
				while ((len = zin.read(buf)) != -1)
				{
					out.write(buf, 0, len);
				}
			}
			zin.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
				if (zin != null)
					zin.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("FINISH");
	}
	
	public static void updateWarFile(File zipFile)
	{
		
		ZipInputStream zin = null;
		ZipOutputStream out = null;
		
		try
		{
			File tempFile = File.createTempFile(zipFile.getName(), null);
			tempFile.delete();
			
			boolean renameOk = zipFile.renameTo(tempFile);
			if (!renameOk)
			{
				throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
			}
			byte[] buf = new byte[1024];
			
			zin = new ZipInputStream(new FileInputStream(tempFile));
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			
			ZipEntry entry = zin.getNextEntry();
			while ((entry = zin.getNextEntry()) != null)
			{
				String name = entry.getName();
				out.putNextEntry(new ZipEntry(name));
				
				int len;
				while ((len = zin.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				;
			}
			
			for (File file : new File(LIB_FOLDER).listFiles())
			{
				InputStream in = new FileInputStream(file);
				out.putNextEntry(new ZipEntry(FOLDER + file.getName()));
				
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			tempFile.delete();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				zin.close();
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}