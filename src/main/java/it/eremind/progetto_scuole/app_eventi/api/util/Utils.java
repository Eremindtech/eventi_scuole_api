package it.eremind.progetto_scuole.app_eventi.api.util;

import java.io.*;
import java.util.*;
import java.text.*;
import java.security.*;


public class Utils {

	public static final int CAL_FRMT_YYYYMMDD = 0;
	public static final int CAL_FRMT_DDMMYYYY = 1;
	public static final int CAL_FRMT_YYMMDD = 2;
	public static final int CAL_FRMT_DDMMYY = 3;
	public static final int CAL_FRMT_YYYY_MM_DD = 4;
	public static final int CAL_FRMT_DD_MM_YYYY = 5;
	public static final int CAL_FRMT_YY_MM_DD = 6;
	public static final int CAL_FRMT_DD_MM_YY = 7;

	public static final int DBL_FRMT_IT = 0;
	public static final int DBL_FRMT_IT_NO_CS = 1;
	public static final int DBL_FRMT_IT_CENT = 2;
	public static final int DBL_FRMT_US = 3;
	public static final int DBL_FRMT_US_NO_CS = 4;
	public static final int DBL_FRMT_US_CENT = 5;
	public static final int DBL_FRMT_ZERO2NULL = 8;

	public static final long MILLISECS_PER_DAY=24*60*60*1000;
	
	public static final String[] monthList_it = new String[] {	
																											"Gennaio",
																											"Febbraio",
																											"Marzo",
																											"Aprile",
																											"Maggio",
																											"Giugno",
																											"Luglio",
																											"Agosto",
																											"Settembre",
																											"Ottobre",
																											"Novembre",
																											"Dicembre" };

	public static final String[] monthList_en = new String[] {	
																											"January",
																											"February",
																											"March",
																											"April",
																											"May",
																											"June",
																											"July",
																											"August",
																											"September",
																											"October",
																											"November",
																											"Dicember" };



	public static final String[] dayList;
	
	static {
		dayList=new String[31];
		for (int i=0; i<31;i++) {
			dayList[i]=padLeft(Integer.toString(i+1), 2, '0');
		}	
	}	
	
	private Utils() {
	}
	
	/**
	 * Utility - Convert null strings into empty strings
	 * @param s
	 * @return s or empty string
	 */
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

	public static String ltrimZeroes( String s ) {
		
		int len = s == null ? 0 : s.length();
		if ( len == 0 )
			return "";
			
		char[] sa = s.toCharArray();
		int idx = 0;
		for ( int i = 0; i < len; i++ ) {
			char ch = sa[i];
			if ( ch == '0' ) {
				idx++;
			}	
			else {
				break;
			}	
		}
		
		if ( idx == 0 )
			return s;

		if ( idx >= len )
			return "";
			
		return s.substring( idx );		
	
	}

	/**
	** truncSpaces
	** @param s
	** @return s with no spaces
	**/
	public static String truncSpaces(String s) {
		
		int len = s == null ? 0 : s.length();
		if ( len == 0 )
			return "";
		
		char[] ca = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for ( char c : ca ) {
			if ( c != ' ' )
				sb.append( c );
		}

		return sb.toString();		
	
	}

	/**
	** trunc Redundand Spaces
	** @param s
	** @return s with no redundant spaces
	**/
	public static String truncRedntSpaces(String s) {
		
		s = s == null ? "" : s.trim();
		int len = s.length();
		if ( len == 0 )
			return s;
		
		char[] ca = s.toCharArray();
		StringBuilder sb = new StringBuilder();

		char pc = '?';
		for ( char c : ca ) {
			if ( ( c != ' ' ) || ( pc != ' ' ) ) { 
				sb.append( c );
				pc = c;
			}	
		}

		return sb.toString();		
	
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

	public static Date parseDate( String value, int frmt ) {
		Calendar cal = parseCalendar( value, frmt );
		return cal == null ? null : cal.getTime();
	}	



	public static Date parseDate( String value ) {

		String	tmp = trim2Empty( value );
		if ( tmp.length() == 10 ) {
			return parseDate( tmp, CAL_FRMT_DD_MM_YYYY );
		}
		return null;
					

	}

	public static String formatDate( Date value, DateFormat sdf ) {
		
		if ( value == null )
			return "";
		return sdf.format( value );

	}


	public static String formatDate( Date value ) {
		
		if ( value == null )
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
		return formatDate( value, sdf );

	}


	public static String formatDate( Date value, int frmt ) {
		return formatDate( value, frmt, '/' );
	}	
	
	
	public static String formatDate( Date value, int frmt, char sep ) {
		
		if ( value == null )
			return null;
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime( value );
		return formatCalendar( cal, frmt, sep );
	}

	public static Calendar parseCalendar( String value, String sFrmt ) {
		
		int frmt= CAL_FRMT_DD_MM_YYYY; 
		if (sFrmt!=null) {
			if (sFrmt.length()==8) {
				if ('y'==sFrmt.charAt(0))
					frmt=CAL_FRMT_YY_MM_DD;
				else 	
					frmt=CAL_FRMT_DD_MM_YY;
			}	
			else if (sFrmt.length()==10) {
				if ('y'==sFrmt.charAt(0))
					frmt=CAL_FRMT_YYYY_MM_DD;				
			}	
		}
		return parseCalendar(value, frmt);	
			
		
	}	

	/**
	** Parse String to Calendar
	** @param value
	** @param frmt (separator '/' stay for '/', '-', '.' ) 
	** <p>frmt=0 yyyyMMdd</p>
	** <p>frmt=1 ddMMyyyy</p>
	** <p>frmt=2 yyMMdd</p>
	** <p>frmt=3 ddMMyy</p>
	** <p>frmt=4 yyyy/MM/dd</p>
	** <p>frmt=5 dd/MM/yyyy</p>
	** <p>frmt=6 yy/MM/dd</p>
	** <p>frmt=7 dd/MM/yy</p>
	
	**/



	public static Calendar parseCalendar( String value, int frmt ) {
		
		int len = value == null ? 0 : value.length();
		
		String yy = null;
		String MM = null;
		String dd = null;
		
		if ( ( frmt == CAL_FRMT_YYYYMMDD ) || ( frmt == CAL_FRMT_DDMMYYYY ) || ( frmt == CAL_FRMT_YY_MM_DD ) || ( frmt == CAL_FRMT_DD_MM_YY ) ) {
			if ( len != 8 ) {
				return null;
			}
			if ( frmt == CAL_FRMT_YYYYMMDD ) {
				yy = value.substring( 0, 4 );
				MM = value.substring( 4, 6 );
				dd = value.substring( 6, 8 );
			}
			else if ( frmt == CAL_FRMT_DDMMYYYY ) {
				yy = value.substring( 4, 8 );
				MM = value.substring( 2, 4 );
				dd = value.substring( 0, 2 );
			}
			else if ( frmt == CAL_FRMT_YY_MM_DD ) {
				yy = value.substring( 0, 2 );
				MM = value.substring( 3, 5 );
				dd = value.substring( 6, 8 );
			}			
			else { // CAL_FRMT_DD_MM_YY
				yy = value.substring( 6, 8 );
				MM = value.substring( 3, 5 );
				dd = value.substring( 0, 2 );
			}			
		}	 
		else if ( ( frmt == CAL_FRMT_YYMMDD ) || ( frmt == CAL_FRMT_DDMMYY ) ) {
			if ( len != 6 ) {
				return null;
			}
			if ( frmt == CAL_FRMT_YYMMDD ) {
				yy = value.substring( 0, 2 );
				MM = value.substring( 2, 4 );
				dd = value.substring( 4, 6 );
			}
			else { // CAL_FRMT_DDMMYY
				yy = value.substring( 4, 6 );
				MM = value.substring( 2, 4 );
				dd = value.substring( 0, 2 );
			}
		}	 
		else if ( ( frmt == CAL_FRMT_YYYY_MM_DD ) || ( frmt == CAL_FRMT_DD_MM_YYYY ) ) {
			if ( len != 10 ) {
				return null;
			}
			if ( frmt == CAL_FRMT_YYYY_MM_DD ) {
				yy = value.substring( 0, 4 );
				MM = value.substring( 5, 7 );
				dd = value.substring( 8, 10 );
			}
			else { // CAL_FRMT_DD_MM_YYYY
				yy = value.substring( 6, 10 );
				MM = value.substring( 3, 5 );
				dd = value.substring( 0, 2 );
			}
		}	 
		else {
			throw new IllegalArgumentException( "parseCalendar: Invalid format=" + frmt );
		}	
		return parseCalendar( yy, MM, dd );

	}



	/**
	** parse Calendar
	** @param yy
	** @param MM
	** @param dd 
	** @return new GregorianCalendar if valid or null
	**/

	public static Calendar parseCalendar( String yy, String MM, String dd ) {
		
		if ( ( yy == null ) || ( ( yy.length() != 2 ) && ( yy.length() != 4 ) ) )
			return null;
		if ( ( MM == null ) || ( MM.length() != 2 ) )
			return null;
		if ( ( dd == null ) || ( dd.length() != 2 ) )
			return null;

		int year = 0;
		try {
			year = Integer.parseInt( yy );
		}
		catch ( Exception ex ) {
			return null;
		}	
		if ( year < 100 ) {
			year += 2000;
		}

		int month = 0;	
		try {	
			month = Integer.parseInt( MM );	
		}
		catch ( Exception ex ) {
			return null;
		}	
		if ( ( month < 1 ) || ( month > 12 ) )
			return null;
			
		int day = 0;
		try {	
			day = Integer.parseInt( dd );	
		}
		catch ( Exception ex ) {
			return null;
		}	
		if ( ( day < 1 ) || ( day > 31 ) )
			return null;
		
		switch ( month ) {
			case 4:
			case 6:
			case 9:
			case 11: {
				if ( day == 31 )
					return null;
				break;		
			}	
			case 2: {
				int maxDay = 28;
				if ( ( year % 4 ) == 0 ) {
					if ( ( ( year % 100 ) != 0 ) || ( ( year % 400 ) == 0 ) ) {
						maxDay = 29;
					}
				}
				
				if ( day > maxDay )
					return null; 
									
			}	
		}	
		
		return new GregorianCalendar( year, month-1, day );	
	
	}
	
	// return self 4 comodita'...
	public static Calendar addHHmmssToCal(Calendar cal, String HHmmss) {
		
		if (cal==null)
			throw new IllegalArgumentException("addHHmmssToCal: null calendar");
			
			
		int len=HHmmss==null?0:HHmmss.length();
		if (len!=6 && len!=8)
			throw new IllegalArgumentException( "addHHmmssToCal: Invalid HHmmss=" + HHmmss );
		
		int HH=Integer.parseInt(HHmmss.substring(0, 2));
		int offset=2;
		if (len==8)
			offset++;			
		int mm=Integer.parseInt(HHmmss.substring(offset, offset+2));
		offset+=2;
		if (len==8)
			offset++;
		int ss=Integer.parseInt(HHmmss.substring(offset, offset+2));
		cal.set(Calendar.HOUR_OF_DAY, HH);
		cal.set(Calendar.MINUTE, mm);
		cal.set(Calendar.SECOND, ss);
		return cal;
		
	}	
	
	
	public static String formatCalendar( Calendar cal, int frmt ) {
		return formatCalendar( cal, frmt, '/' );
	}	 

	public static String formatCalendar( Calendar cal, int frmt, char sep ) {
		
		if ( cal == null )
			return null;
		
		int year = cal.get( Calendar.YEAR );
		if ( ( frmt == CAL_FRMT_YYMMDD ) || ( frmt == CAL_FRMT_DDMMYY ) || ( frmt == CAL_FRMT_YY_MM_DD ) || ( frmt == CAL_FRMT_DD_MM_YY ) ) {
			year %= 100;
		}
		int month = cal.get( Calendar.MONTH ) + 1;
		int day = cal.get( Calendar.DATE );
		StringBuilder sb = new StringBuilder();
		if ( frmt == CAL_FRMT_YYYYMMDD ) {
			sb.append( padLeft( Integer.toString( year ), 4, '0' ) );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_DDMMYYYY ) {
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( year ), 4, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_YYMMDD ) {
			sb.append( padLeft( Integer.toString( year ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_DDMMYY ) {
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) );
			sb.append( padLeft( Integer.toString( year ), 2, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_YYYY_MM_DD ) {
			sb.append( padLeft( Integer.toString( year ), 4, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_DD_MM_YYYY ) {
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( year ), 4, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_YY_MM_DD ) {
			sb.append( padLeft( Integer.toString( year ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) );
		}	
		else if ( frmt == CAL_FRMT_DD_MM_YY ) {
			sb.append( padLeft( Integer.toString( day ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( month ), 2, '0' ) ).append( sep );
			sb.append( padLeft( Integer.toString( year ), 2, '0' ) );
		}	
		else {
			throw new IllegalArgumentException( "formatCalendar: Invalid format=" + frmt );
		}	
		
		return sb.toString();

	}	 


	public static Double parseDoubleObj( String value, int frmt ) {
		
		double d = parseDouble( value, frmt & 7 );
			// zero 2 null )
		if ( ( d == 0d ) && ( ( frmt & DBL_FRMT_ZERO2NULL ) != 0 ) )
			return null;
		
		return new Double( d );
		
	}


	public static double parseDouble( String value, int frmt ) {
		
		DecimalFormat df = null;
		if ( ( frmt == DBL_FRMT_IT ) || ( frmt == DBL_FRMT_IT_NO_CS ) || ( frmt == DBL_FRMT_IT_CENT ) ) {
			df = (DecimalFormat) NumberFormat.getInstance( Locale.ITALIAN );
			df.applyLocalizedPattern( "#.##0,00" );
		}	
		else if ( ( frmt == DBL_FRMT_US ) || ( frmt == DBL_FRMT_US_NO_CS ) || ( frmt == DBL_FRMT_US_CENT ) ) {
			df = (DecimalFormat) NumberFormat.getInstance( Locale.US );
			df.applyLocalizedPattern( "#,##0.00" );
		}
		else {
			throw new IllegalArgumentException( "parseDouble: Invalid format=" + frmt );
		}	
		
		double d = parseDouble( value, df );
		if ( ( d != 0d ) && ( ( frmt == DBL_FRMT_IT_CENT ) || ( frmt == DBL_FRMT_US_CENT ) ) )
			d /= 100;
		
		return d;
		
	}
	
	public static double parseDouble( String value, DecimalFormat df ) {
		
		String tmp = null;
		double d = 0d;
		
		try {
			tmp = trim2Empty( value );
			if ( tmp.length() > 0 ) {
				d = df.parse( tmp ).doubleValue();
			}
		}
		catch ( Exception es ) {
		}
		
		return d;		

	}

	public static double parseDouble( String value ) {
		
		return parseDouble( value, DBL_FRMT_IT );

	}

	public static String formatDouble( double value, DecimalFormat df ) {
		
		if ( Math.abs(value) < 0.001 )
			return null;
			
		return df.format( value );

	}


	public static String formatDouble( double d, int frmt ) {
		
		DecimalFormat df = null;
		if ((frmt == DBL_FRMT_IT_CENT) || (frmt == DBL_FRMT_US_CENT)) {
			df = (DecimalFormat) NumberFormat.getInstance( Locale.ITALIAN );
			df.applyLocalizedPattern( "##0" );
			d*=100;
		}	
		else if ( ( frmt == DBL_FRMT_IT ) || ( frmt == DBL_FRMT_IT_NO_CS ) ) {
			df = (DecimalFormat) NumberFormat.getInstance( Locale.ITALIAN );
			if ( frmt == DBL_FRMT_IT_NO_CS )
				df.applyLocalizedPattern( "##0,00" );
			else
				df.applyLocalizedPattern( "#.##0,00" );
		}	
		else if ( ( frmt == DBL_FRMT_US ) || ( frmt == DBL_FRMT_US_NO_CS ) ) {
			df = (DecimalFormat) NumberFormat.getInstance( Locale.US );
			if ( frmt == DBL_FRMT_US_NO_CS )
				df.applyLocalizedPattern( "##0.00" );
			else
				df.applyLocalizedPattern( "#,##0.00" );
		}
		else {
			throw new IllegalArgumentException( "formatDouble: Invalid format=" + frmt );
		}	
		
		return formatDouble( d, df );

	}

	public static String formatDouble( double d ) {
		return formatDouble( d, 0 );
	}
	

	public static String buildCro( String cro ) {
		StringBuilder sb=new StringBuilder();
		sb.append(cro);
		sb.append(padLeft(Integer.toString(evalCinCro(cro)),2,'0'));
		return sb.toString();
	}
	
	public static int evalCinCro (String cro) {
		if (cro==null || cro.length()!=9)
			throw new IllegalArgumentException("Invalid cro");
		return Integer.parseInt(cro)%13;
	}	

	public static String buildMavCode( String abi, String partita, double amount ) {
		StringBuilder sb=new StringBuilder(20);
		int cin10=evalCin10(abi, partita, amount);
		sb.append(abi).append(partita).append(Integer.toString(cin10));
		long cin93=Long.parseLong(sb.toString())%93;
		sb.append(padLeft(Long.toString(cin93),2,'0'));
		return sb.toString(); 			
		
	}		
	
	public static int evalCin10(String abi, String partita, double amount) {
		if (abi==null || abi.length()!=5)
			throw new IllegalArgumentException("Invalid abi");
		
			
		if (partita==null || partita.length()!=9)
			throw new IllegalArgumentException("Invalid partita");
		
		if (amount<=0)
			throw new IllegalArgumentException("Invalid amount:"+amount);
		
		/*
		char[] cabi=abi.toCharArray();
		if ((cabi[0]=='8')||(cabi[0]=='9')){
			cabi[0]-=8;
			abi=new String(cabi);			
		}	
		*/
			
		long importo=(long) (amount*100);

		long l1=Long.parseLong(abi);
		long l2=Long.parseLong(partita);
		long tot=l1+l2+importo;
		
		String s3=String.valueOf(tot);
		//System.out.println("cin10:"+l1+";"+l2+";"+importo+";"+s3);
		tot=0;
		int nc=s3.length();
		boolean disp=(nc&1)==1;
		
		for(int k=0; k<nc; k++) {
			int toAdd=s3.charAt(k)-48;
			if (disp) {
				toAdd<<=1;
				if (toAdd>=10) {
					toAdd-=9;
				}
			}
			tot+=toAdd;
			disp=!disp; 
		}
			
		int rem=(int) tot%10;
		int cin10=rem==0 ? 0 : (10-rem);
		return cin10;
	}

	/**
	** Determina data Pasqua secondo metodo di Gauss (vedi Wikipedia)
	** @param year - Anno compreso tra 1900 e 2199 
	** @return data pasqua
	**/
	
	
	public static Calendar getEasterDate( int year ) {
		
		if ( ( year < 1900 ) || ( year > 2199 ) )
			throw new IllegalArgumentException( "Year not between 1900 and 2199" );
		
		int M = 24;
		int N = year < 2100 ? 5 : 6;
		int a = year % 19;
		int b = year % 4;
		int c = year % 7;
		
		int d = ( 19*a + M ) % 30; 
		int e = ( 2*b + 4*c + 6*d + N ) % 7;
		int d_e = d + e;
		
		int month = 0;
		int day = 0;
		
		
		if ( d_e < 10 ) {
			month = 2;
			day = d_e + 22;
		}
		else {
			month = 3;
			day = d_e - 9;
			if ( ( day == 26 ) || ( ( day == 25 ) && ( d == 28 ) && ( e == 6 ) && ( a > 10 ) ) )
				day -= 7;
		}	
	
		return new GregorianCalendar( year, month, day );				
		
		
		
	}



	/**
	** Ritorna vero se giorno festivo
	** @param cal - data riferimento
	** @param isSatHoliday - flag sabato festivo
	** @return true se festivo
	**/
	
	public static boolean isHoliday( Calendar cal, boolean isSatHoliday ) {
		
		/*
		if ( workman.equals( "toti" ) || workman.equals( "fabio" ) )
			return false;
		*/
		
		if ( cal == null )
			throw new IllegalArgumentException( "Calendar null" );
			
		int dow = cal.get( Calendar.DAY_OF_WEEK );
		if ( ( dow == 1 )	|| ( ( dow == 7 ) && isSatHoliday ) )
			return true;
		
		int month = cal.get( Calendar.MONTH );
		int day = cal.get( Calendar.DATE );
		int year = cal.get( Calendar.YEAR );
		
		switch ( month ) {
		
				// JAN
			case 0 : {
				if ( ( day == 1 ) || ( day == 6 ) )
					return true;
				break;
			}	
				// MAR -> solo per 2011
			case 2 : {
				if( ( day == 17 ) && ( year == 2011 ) )
					return true;
				break;
			}
				// APR
			case 3 : {
				if ( day == 25  )
					return true;
				break;
			}	
				// MAY
			case 4 : {
				if ( day == 1  )
					return true;
				break;
			}	
				// JUN
			case 5 : {
				if ( day == 2  )
					return true;
				break;
			}	
				// AUG
			case 7 : {
				if ( day == 15  )
					return true;
				break;
			}	
				// NOV
			case 10 : {
				if ( day == 1  )
					return true;
				break;
			}	
				// DEC
			case 11 : {
				if ( ( day == 8 ) || ( day == 25 ) || ( day == 26 ) )
					return true;
				break;
			}	
		
		}
			
		if ( ( dow == 2 ) && ( ( month == 2 ) || ( month == 3 ) ) ) {
			Calendar calEaster = getEasterDate( cal.get( Calendar.YEAR ) );
			calEaster.add( Calendar.DATE, 1 );
			if ( ( month == calEaster.get( Calendar.MONTH ) ) && ( day == calEaster.get( Calendar.DATE ) ) )
				return true;
		}
		
		
		return false;
		
	}	


	/**
	** Torna nr giorni lavorativi tra le 2 date (data inizio esclusa e data fine inclusa)
	** Se le date coincidono il risultato e' quindi 0
	** @param from - data iniziale
	** @param from - data finale
	** @param isSatHoliday - flag sabato festivo
	** @return nr giorni lavorativi (negativo se data iniziale maggiore di data finale)
	**/

	public static int getNrWorkingDays( Calendar from, Calendar to, boolean isSatHoliday ) {
		
		if ( ( from == null ) || ( to == null ) )
			throw new IllegalArgumentException( "Calendar from or to null" );
		
		int check = to.get( Calendar.YEAR ) - from.get( Calendar.YEAR );
		if ( check == 0 ) {		
			check = to.get( Calendar.MONTH ) - from.get( Calendar.MONTH );
			if ( check == 0 ) {
				check = to.get( Calendar.DATE ) - from.get( Calendar.DATE );
			}
		}
			// same day
		if ( check == 0 )
			return 0;
		
		Date tFrom = from.getTime();
		
		check = check > 0 ? +1 : -1;
		int result = 0;
		boolean eol = false; 

		while ( ! eol ) {

			from.add( Calendar.DATE, check );
			
			if ( ! isHoliday( from, isSatHoliday ) )
				result++;
			
			eol =	( from.get( Calendar.DATE ) == to.get( Calendar.DATE ) ) &&
						( from.get( Calendar.MONTH ) == to.get( Calendar.MONTH ) ) && 
						( from.get( Calendar.YEAR ) == to.get( Calendar.YEAR ) );
		
		}
			
			// reset from a stato originale
		from.setTime( tFrom ); 			
		
		return result*check ;

	}	


	/**
	** Torna nr giorni tra le 2 date (data inizio esclusa e data fine inclusa) indipendentemente
	** da ora minuti etc. 
	** Se le date coincidono il risultato e' quindi 0; se appartengono a due giorni consecutivi
	** anche se per sola differenza di 1 millesimo di secondo il risultato e' 1
	** @param from - data iniziale
	** @param from - data finale
	** @return nr giorni (negativo se data iniziale maggiore di data finale)
	**/

	public static int getNrDays( Calendar from, Calendar to ) {
		
		
		if ( ( from == null ) || ( to == null ) )
			throw new IllegalArgumentException( "Calendar from or to null" );

		Date tFrom = from.getTime();
		Date tTo = to.getTime();
		
		from.set( Calendar.HOUR_OF_DAY, 0 );
		from.set( Calendar.MINUTE, 0 );
		from.set( Calendar.SECOND, 0 );
		from.set( Calendar.MILLISECOND, 1 );

		to.set( Calendar.HOUR_OF_DAY, 0 );
		to.set( Calendar.MINUTE, 0 );
		to.set( Calendar.SECOND, 0 );
		to.set( Calendar.MILLISECOND, 1 );
		//System.out.println( to.getTimeInMillis() - from.getTimeInMillis() );
		long diff = ( to.getTime().getTime() - from.getTime().getTime() ) / MILLISECS_PER_DAY;
		
			// ripristino valori originali
		from.setTime( tFrom );
		to.setTime( tTo );
		
		return (int) diff;

	}	
	
	
	/**
	** @param from - data iniziale
	** @param from - data finale
	** @return se dataIniziale successiva a qualla finale
	**/

	public static int dateCompare( Calendar data1, Calendar data2 ) {
		
		
		if ( ( data1 == null ) || ( data2 == null ) )
			throw new IllegalArgumentException( "Calendar from or to null" );

		Date t1 = data1.getTime();
		Date t2 = data2.getTime();
		
		
		data1.set( Calendar.SECOND, 0 );
		data1.set( Calendar.MILLISECOND, 1 );

		
		data2.set( Calendar.SECOND, 0 );
		data2.set( Calendar.MILLISECOND, 1 );
		
		int result = data1.compareTo( data2 );//Torna 0 se le 2 date sono identiche al minuto; < 0 se <data1> e' prima di <data2> 
		
		
			// ripristino valori originali
		data1.setTime( t1 );
		data2.setTime( t2 );
		
		return result;

	}	


	public static char evalCin( String abi, String cab, String conto ) {
		
		// cStrRif = stringa riferimento posizioni dispari 
		String cStrRif = "0100050709131517192102041820110306081214161022252423";
		
		
		StringBuilder sb = new StringBuilder(22);
		sb.append( abi ).append( cab ).append( conto );
		String s = sb.toString();

		int nTot = 0;
		
		for ( int i = 0; i < 22; i++ ) {
			
			char c = s.charAt( i );
			
				// posizione pari
			if ( i % 2 == 1 ) {
				
					// Caso Alfabetico : A=0, B=1, C=2, ..., Z=25 
					// Incremento nTot del valore corrispondente
				if ( c == '-' )
					nTot += 26;
				else if ( c == '.' )
					nTot += 27;
				else if ( c == ' ' )
					nTot += 28;
				else if ( Character.isLetter( c ) )
					nTot += ( c - 65 );
					// numerico
				else
					nTot += (c - 48 );	
			
			}
				 /* Caso Posizione Dispari
				** Nel caso carattere alfabetico e numerico si determina un 
				** numero intermedio Nx analogamente al caso pari e si incrementa
				** nTot del valore contenuto nei 2 caratteri della stringa cStrRif
				** partendo dalla posizione Nx * 2 + 1   
				** Cosi ad es. A e 0 -> 1 ; B e 1 -> 0 ; C e 2 -> 5 ; Z -> 23
				*/
			else {
				
				if ( c == '-' )
					nTot += 27;
				else if ( c == '.' )
					nTot += 28;
				else if ( c == ' ' )
					nTot += 26;
				else if ( Character.isLetter( c ) ) {
					int nTmp = ( c - 65 ) * 2;
					String tmp = cStrRif.substring( nTmp, nTmp + 2 );
					nTot += Integer.parseInt( tmp );
					// numerico
				}
				else {
					int nTmp = ( c - 48 ) * 2;
					String tmp = cStrRif.substring( nTmp, nTmp + 2 );
					nTot += Integer.parseInt( tmp );
				}
				
			}	
			
			//System.out.println( nTot );
			
		}	
		
		return (char) ( ( nTot % 26 ) + 65 ); 
		
	}		
	
	// TODO MAX
	public static String[] split( String line, char[] seps) {
		
		if (line==null)
			return null;
		if (seps==null||seps.length==0)
			return new String[]{line};
		
		int len=line.length();
		List<String> l=new ArrayList<String>();
		StringBuilder sb=new StringBuilder(len);
		int idx=0;
		
		for (int i=0; i<len; i++) {
			boolean found=false;
			char c=line.charAt(i);
			for (char s: seps) {
				if (c==s) {
					l.add(sb.toString());
					
					sb.setLength(0);
					found=true;
					break;	
				}
			}
			if (!found)
				sb.append(c);
		}	
		int size=l.size();
		String[] res=new String[size+1];
		for (int i=0; i<size; i++) {
			res[i]=l.get(i);
		}
		res[size]=sb.toString();	
		/*
		for (int i=0; i<=size;i++) {
			System.out.println(">"+res[i]+"<");
		}	
		*/
		
		return res;		
		
	}


	public static void splitMsg(String msg, List<String> rList) {
		
		int len=msg==null?0:msg.length();
		if (len==0)
			return;
		StringBuilder sb=new StringBuilder(len);
		boolean reset=true;	
		char prevC=0;
		for (int i=0; i<len; i++) {	
			char c=msg.charAt(i);
			if (prevC==0 || prevC=='}' || c=='$') {
				if (sb.length()>0) {
					rList.add(sb.toString());
					sb.setLength(0);
				}	
			}
			sb.append(c);
			prevC=c;
			
		}
		
		if (sb.length()>0) {
			rList.add(sb.toString());
		}	
		
	}	

	public static String buildMsg(List<String> lst, Map<String,String> map) {
		
		int size=lst==null?0:lst.size();
		StringBuilder sb=new StringBuilder(1024);
		for (int i=0; i<size; i++) {
			String s=lst.get(i);
			String x=map.get(s);
			sb.append(x==null?s:x);
		}	
		return sb.toString();		
	}	

	/**
	** numeric -> 1
	** alpha upper -> 2
	** alpha lower -> 4
	** other -> 0
	** @param c
	**/
	public static int getCharType( char c ) {
	
		if ( ( c >= '0' ) && ( c <= '9' ) )
			return 1;

		if ( ( c >= 'A' ) && ( c <= 'Z' ) )
			return 2;

		if ( ( c >= 'a' ) && ( c <= 'z' ) )
			return 4;

		
		return 0;	
	
	}
	/**
	** check alpha/numeric or both
	** type=1 numeric; type=2 alpha upper;type=4 alpha lower; type<=0 -> type=4|2|1  
	** @param s
	** @param type
	**/
	public static boolean isAlphaNum( String s, int type ) {
		
		if ( type <= 0 )
			type = 7;
		
		if ( ( s == null ) || ( s.length() == 0 ) )
			return false;
		int len = s.length();
		for ( int i = 0; i < len; i++ ) {
			if ( ( getCharType( s.charAt(i) ) & type ) == 0 ) {
				return false;
			}	
		}		
		return true;
	}

	public static boolean isValidDbColName(String colName) {
		int len=colName==null? 0 : colName.length();
		if (len==0)
			return false;
		
		for (int i=0; i<len; i++) {
			char c=colName.charAt(i);
			if (c!='_' && getCharType(c)==0)
				return false;	
		}		
		return true;
	}	

	/**
	** truncSpaces
	** @param s
	** @return s with no spaces
	**/
	public static String labelize(String s, char sepChar) {
		
		int len = s == null ? 0 : s.length();
		if ( len == 0 )
			return "";
		
		char[] ca = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		char prevC='?';
		for ( char c : ca ) {
			if (!isWhiteSpace(c)) {
				sb.append(c);
			}
			else if (!isWhiteSpace(prevC)) {
				sb.append(sepChar);
			}	
			prevC=c;
		
		}

		return sb.toString();		
	
	}
	
	public static boolean isWhiteSpace( char c ) {
		
		return 	( c == 32 ) ||
						( c == 9  ) ||
						( c == 10 ) ||
						( c == 13 );
						
	}	
		
	
	
	public static String digest(String value) throws Exception {
		return digest(value, "SHA-256");
	}
	
	public static String digest(String value, String alg) throws Exception {

		MessageDigest md=MessageDigest.getInstance(alg);
		byte[] result=md.digest(value.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(result);
		
	}	
	
	/**
	 * @param start - the start year, will be included
	 * @param len - the length of the generated list 
	 */
	public static String[] getYearList(int start, int len) {
		String[] res=new String[len];
		for (int i=0; i<len;i++) {
			res[i]=Integer.toString(start+i);
		}
		return res;
	}	

	public static String escapeMySql(String s) {
		int len=s==null?0:s.length();
		if (len==0)
			return s;
			
		StringBuilder sb=new StringBuilder(len+10);
		
		for (int i=0; i<len; i++) {
			char c=s.charAt(i);
			if (c=='\'')
				sb.append('\\');
			sb.append(c);	
			
		}	
		return sb.toString();
		
	}		
	
}



