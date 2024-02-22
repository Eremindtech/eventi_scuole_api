package it.eremind.progetto_scuole.app_eventi.api.util;

import java.io.*;
import java.util.*;
import java.text.*;
import java.security.*;




public final class Utility {


	final static char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	final static SimpleDateFormat sdfyyMMddHHmmss = new SimpleDateFormat( "yyMMddHHmmss" );
	final static SimpleDateFormat sdfFile = new SimpleDateFormat( "ddMMyyyy_HHmmss" );


	private Utility() {
	}
	
	
	public static String d2yyMMddHHmmss( Date d ) {
		
		String result = "000000000000";
		
		if ( d == null ) {
			return result;
		}
		
		try {
			result = sdfyyMMddHHmmss.format( d );
		}
		catch( Exception e ) {
		}
		
		return result;
		
	}
	
	public static String d2FileString( Date d ) {
		
		String result = "000000000000000";

		if ( d == null ) {
			return result;
		}

		try {
			result = sdfFile.format( d );
		}
		catch( Exception e ) {
		}

		return result;
		
	}
	
	
	
	public static String encode( String value, String alg ) throws Exception {
		
		MessageDigest md = MessageDigest.getInstance( alg );
		byte[] b = md.digest( value.getBytes() );
		
		return bytesHex( b );
		
	}
	
	
	public static String bytesHex( byte[] value ){
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < value.length; i++ )
			byte2hex( value[ i ], sb );
		return sb.toString();
	}
	
	
	public static void byte2hex( byte b, StringBuilder sb ) {
			
		int high = ( ( b & 0xf0 ) >> 4 );
		int low = ( b & 0x0f );
		sb.append( HEX_CHARS[ high ] );
		sb.append( HEX_CHARS[ low ] );
	
	}
	
	
	/**
	 ** 
	 ** @return true if s is null or a empty string (trimmed)
	**/
	public static boolean isNullOrEmpty( String s ) {
		
		if ( ( s == null ) || ( s.trim().length() == 0 ) )
			return true;
		
		return false;
		
	}
	
	
	/**
	** Utility
	** @param s
	** @return s or empty string
	**/
	public static String null2Empty(String s) {
		return s==null ? "" : s;
	}

	/**
	** Utility
	** @param s
	** @return trim s or empty string
	**/
	public static String trim2Empty(String s) {
		return s==null ? "" : s.trim();
	}


	public static String padRight(String s, int len) {
		return padRight(s, len, ' ');
	}

	public static String padRight(String s, int len, char c) {
		
		if (len<=0)
			throw new IllegalArgumentException("Invalid len:"+len);
			
		int tmp=len;
		if (s!=null)
			tmp-=s.length();
		
		if (tmp>0) {
			StringBuilder sb=new StringBuilder(len) ;
			if (s!=null)
				sb.append(s);
			for (int k=0; k<tmp; k++)
				sb.append(c);
			return sb.toString();
		}
		else if (tmp<0) {
			return s.substring(0, len);
		}		

		return s;
		
	}	 
	
	public static String padLeft(String s, int len) {
		return padLeft(s, len, ' ');
	}

	public static String padLeft(String s, int len, char c) {

		if (len<=0)
			throw new IllegalArgumentException("Invalid len:"+len);
			
		int tmp=len;
		if (s!=null)
			tmp-=s.length();
		
		if (tmp>0) {
			
			StringBuilder sb=new StringBuilder(len);
			for (int k=0; k<tmp; k++)
				sb.append(c);
			if (s !=null)
				sb.append(s);
			return sb.toString(); 
		}
		else if (tmp<0) {
			return s.substring(0, len);
		}		

		return s;
		
	}	 



	/**
	** move file (try rename: if fail copy and del)
	** @param fIn
	** @param fOut
	**/
	public static boolean moveFile(File fIn, File fOut) {
		if (fIn.renameTo(fOut)) {
			return true;
		}
		return copyAndDelFile(fIn, fOut);
	}

	/**
	** copy and del
	** @param fIn
	** @param fOut
	**/
	public static boolean copyAndDelFile(File fIn, File fOut) {
		return copyFile(fIn, fOut, true);
	}

	/**
	** copy file
	** @param fIn
	** @param fOut
	**/
	public static boolean copyFile(File fIn, File fOut) {
		return copyFile(fIn, fOut, false);
	}

	/**
	** copy (move) file
	** @param fIn
	** @param fOut
	** @param delOrg
	**/
	public static boolean copyFile(File fIn, File fOut, boolean delOrg) {

		FileInputStream is=null;
		FileOutputStream os=null;
		try
		{
		  byte[] buffer=new byte[8192];
		  is=new FileInputStream(fIn);
		  os=new FileOutputStream(fOut);

		  int i=0;
		  while ((i=is.read(buffer, 0, 8192))>0) {
				os.write(buffer, 0, i);
		  }

		  is.close();
		  is=null;
		  os.close();
		  os=null;

		  
		  return true;
		}
		catch (IOException e) {
		  System.out.println(e.getMessage());
		  return false;
		}
		finally {
		  if (is!=null) try {is.close(); is=null;} catch (Exception ex) {} 
		  if (os!=null) try {os.close(); os=null;} catch (Exception ex) {} 
		  if (delOrg) fIn.delete(); 
		} 
  
	}


	
	
	
	
	public static String escapeJson( String s ) {
		
		if ( isNullOrEmpty( s ) )
			return s;
		
		int len = s.length();
		
		StringBuilder sb = new StringBuilder( len );
		
		for( int i = 0; i < len; i++ ) {
			char c = s.charAt(i);
			if ( ( c == '"' ) || ( c == '\\' ) )
				sb.append( '\\' );
			sb.append( c );
		}
		
		return sb.toString();
		
	}
	
	


}



