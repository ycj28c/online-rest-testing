package util;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Class <code>TimeUtil</code> the Time, Date, Calendar Utility
 * 
 * @author 	RalphYang	
 * @version 1.0 2015/06/22
 * @since 	JDK 1.8
 */
public class TimeUtil {
	private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);
	/**
	 * calculate how many days between begin Date and end Date
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws ParseException 
	 */
	public static long daysBetween(String beginDate, String endDate, String inputFormat){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(inputFormat);  
		LocalDate theBeginDate = LocalDate.parse(beginDate, formatter);
		LocalDate theEndDate = LocalDate.parse(endDate, formatter);
		//Duration oneDay = Duration.between(begin_date, end_date);
		long days = Duration.between(theBeginDate.atTime(0, 0), theEndDate.atTime(0, 0)).toDays(); 
		log.info("** daysBetween: begin_date:{}, end_date:{}, days between:{}",theBeginDate,theEndDate,days);
		return days;
	}
	
    /**
	 * get data at XXX years ago or later
	 * @param years : can be positive or negative
	 * @param dateFormat: output date format, such as "MM/dd/yyyy"
	 * @return String
	 */
    public static String getDateByYears(int years, String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.YEAR, years);
	
	    return f.format(c.getTime());
    }
    
    /**
   	 * get data at XXX years ago or later at certain date
   	 * @param years : can be positive or negative
   	 * @param dateFormat: output date format, such as "MM/dd/yyyy"
   	 * @return String
   	 */
       public static String getDateByYears(String fromDate, int years, String dateFormat){
   	    Format f = new SimpleDateFormat(dateFormat);
   	    Calendar c = stringToCalendar(fromDate, dateFormat);
   	    c.add(Calendar.YEAR, years);
   	
   	    return f.format(c.getTime());
       }
    
    /**
     * get data at XXX months ago or later
     * @param months : can be positive or negative
     * @param dateFormat: output date format, such as "MM/dd/yyyy"
     * @return String
     */
    public static String getDateByMonths(int months, String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.MONTH, months);
	
	    return f.format(c.getTime());
    }
    
    /**
     * get data at XXX months ago or later at certain date
     * @param fromDate
     * @param months : can be positive or negative
     * @param dateFormat : output date format, such as "MM/dd/yyyy"
     * @return
     */
    public static String getDateByMonths(String fromDate, int months, String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = stringToCalendar(fromDate, dateFormat);
	    c.add(Calendar.MONTH, months);
	
	    return f.format(c.getTime());
    }
    
    /**
	 * get data at XXX days ago or later
	 * @param days : can be positive or negative
	 * @param dateFormat: output date format, such as "MM/dd/yyyy"
	 * @return String
	 */
    public static String getDateByDays(int days, String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = Calendar.getInstance();
	    c.add(Calendar.DATE, days);

	    return f.format(c.getTime());
    }
    
    /**
     * get data at XXX days ago or later at certain day
     * @param fromDate
     * @param days
     * @param dateFormat
     * @return
     */
    public static String getDateByDays(String fromDate, int days, String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = stringToCalendar(fromDate, dateFormat);
	    c.add(Calendar.DATE, days);
	
	    return f.format(c.getTime());
    }
	    
	/**
	 * transfer input the date format (such as "MM/dd/yyyy") in page into the db format data (such as "dd/MMM/yyyy")
	 * @param pageDate
	 * @return
	 */
	public static String formatToFormat(String Date, String inputFormat, String outputFormat) {
		if(Date==null||Date.trim().equals("")){
			return null;
		}
		//SimpleDateFormat  dbDateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
		SimpleDateFormat  inputDateFormat = new SimpleDateFormat(inputFormat); 
		SimpleDateFormat  outputDateFormat = new SimpleDateFormat(outputFormat); 	
		String dbDate = null;
		try {
			java.util.Date d1 = inputDateFormat.parse(Date);
			dbDate = outputDateFormat.format(d1);
		} catch (ParseException e) {
			log.error("pageDateToDBdate: Parse dbDateFormat error.");
		}
		return dbDate;
	}
	
    /**
    * transfer the string date to the calendar format
    * @param date
    * @param dateFormat
    * @return
    */
    public static Calendar stringToCalendar(String date, String dateFormat){
    	SimpleDateFormat df=new SimpleDateFormat(dateFormat);
    	Date d = null;
		try {
			d = df.parse(date);
		} catch (ParseException e) {
			log.error("stringToCalendar:",e);
		}
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(d);
    	return cal;
    }
    
    /**
     * get data at today
     * @param dateFormat : output date format, such as "MM/dd/yyyy"
     * @return
     */
    public static String getDateToday(String dateFormat){
	    Format f = new SimpleDateFormat(dateFormat);
	    Calendar c = Calendar.getInstance();
	    
	    return f.format(c.getTime());
    }
      
    /**
     * get data at yesterday
     * @param dateFormat : output date format, such as "MM/dd/yyyy"
     * @return
     */
    public static String getDateYesterday(String dateFormat){
    	return getDateByDays(-1, dateFormat);
    }
    
	/**
     * get data at five years ago
     * @param dateFormat : output date format, such as "MM/dd/yyyy"
     * @return
     */
    public static String getDateBeforeFiveYears(String dateFormat){
    	return getDateByYears(-5, dateFormat);
    }
    
    /**
     * get data at ten years ago
     * @param dateFormat : output date format, such as "MM/dd/yyyy"
     * @return
     */
    public static String getDateBeforeTenYears(String dateFormat){
    	return getDateByYears(-10, dateFormat);
    }
    
    /**
     * return the smaller date
     * @param date1
     * @param date2
     * @param dateFormat
     * @return
     */
    public static String getSmallerDate(String date1, String date2, String dateFormat){
    	Calendar c1 = stringToCalendar(date1, dateFormat);
    	Calendar c2 = stringToCalendar(date2, dateFormat);
    	int flag = c1.compareTo(c2);
    	if(flag == 0 ){
    		return date1;
    	} else if(flag > 0){
    		return date2;
    	} else {
    		return date1;
    	}
    }
    
    /**
	 * Generate random date in a period, include begin date, not include end date
	 * @param beginDate: date string
	 * @param endDate: date string
	 * @param inputFormat: format such as ("yyyy-MM-dd")
	 * @param inputFormat: format such as ("dd-MMM-yyyy")
	 * @return randomDate: data String
	 */
	public static String getRandomDateInPeriod(String beginDate, String endDate, String inputFormat, String outputFormat) {
		//log.info("** getRandomDateInPeriod: beginDate is {}, endDate is {}",beginDate, endDate);
        try {
            SimpleDateFormat format = new SimpleDateFormat(inputFormat);
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            Date newDate = new Date(date);
            return new SimpleDateFormat(outputFormat).format(newDate);
        } catch (Exception e) {
            log.error("** getRandomDateInPeriod:",e);
        }
        return null;
    }
	
	/**
	 * Generate random date in a period, include begin date, not include end date
	 * @param beginDate: date string
	 * @param endDate: date string
	 * @param inputFormat & outputFormat: format such as ("yyyy-MM-dd")
	 * @return randomDate: data String
	 */
	public static String getRandomDateInPeriod(String beginDate, String endDate, String dateFormat) {
		//log.info("** getRandomDateInPeriod: beginDate is {}, endDate is {}",beginDate, endDate);
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());

            Date newDate = new Date(date);
            return new SimpleDateFormat(dateFormat).format(newDate);
        } catch (Exception e) {
            log.error("** getRandomDateInPeriod:",e);
        }
        return null;
    }

	/**
	 * get random long number
	 * @param begin
	 * @param end
	 * @return
	 */
    private static long random(long begin, long end) {
        long rtnn = begin + (long) (Math.random() * (end - begin));
        if (rtnn == begin || rtnn == end) {
            return random(begin, end);
        }
        return rtnn;
    }

    /**
     * convert the java.sql.Timestamp to string
     * @param timestamp
     * @param format
     * @return
     */
	public static String timestampToString(java.sql.Timestamp timestamp, String format) {
		String S = new SimpleDateFormat(format).format(timestamp);
		return S;
	}

	/**
	 * transform the current time to string format as unique name,
	 * why fake, because it could be not unique
	 * @return
	 */
	public static String currentTimeToFakeUniqueString() {
		Calendar c = Calendar.getInstance();
		String fakeUniqueName = "" + c.get(Calendar.YEAR) 
				+ (c.get(Calendar.MONTH)+1) 
	            + c.get(Calendar.DAY_OF_MONTH) 
	            + c.get(Calendar.HOUR_OF_DAY) 
	            + c.get(Calendar.MINUTE) 
	            + c.get(Calendar.SECOND) 
	            + c.get(Calendar.MILLISECOND);
	        return fakeUniqueName;
	}
	
	/**
	 * translate the calendar to expect format
	 * @param c
	 * @param format
	 * @return
	 */
	public static String calendarToString(Calendar c, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String strDate = null;
		if (c != null) {
			strDate = sdf.format(c.getTime());
		} 
//		String time = "" + (c.get(Calendar.MONTH)+1)
//				+"/"
//				+c.get(Calendar.DAY_OF_MONTH)
//				+"/"
//				+c.get(Calendar.YEAR);
		return strDate;
	}
	
	/**
	 * get current time stamp
	 * @return
	 */
	public static String getCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime()); //2014/08/06 16:00:22
	}
	
	/**
	 * convert the milliseconds to time format
	 * @param millis
	 * @return
	 */
	public static String millisecondsToTime(long millis){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");    
		Date resultdate = new Date(millis);
		return sdf.format(resultdate);
	}
}
