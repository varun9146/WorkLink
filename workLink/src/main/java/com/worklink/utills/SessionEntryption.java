package com.worklink.utills;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Component;
import net.sf.json.JSONObject;

@Component
public class SessionEntryption {

	static int startyear = 2016;
	static int startmonth = 6;
	static int startday = 31;
	static int weekmillis = 7 * 24 * 60 * 60 * 1000;
	int timeInMilliseconds;
	int tims;

	private static char IDCHARS[] = { 'A', 'Z', 'B', 'Y', 'C', 'X', 'D', 'W', 'E', 'V', 'F', 'U', 'G', 'T', 'H', 'S',
			'I', 'R', 'J', 'Q', 'K', 'P', 'L', 'O', 'M', 'N', '0', '9', '1', '8', '2', '7', '3', '6', '4', '5' };

	/*
	 * public static void main (String[] arg) { Scanner s = new Scanner(System.in);
	 * System.out.println("Enter sessionId"); int sid = s.nextInt();
	 * System.out.println("EnterUserId"); int uid = s.nextInt(); s.close();
	 * SessionEntryption session = new SessionEntryption(); JSONObject sessionObj =
	 * session.encryptSessionId(sid,uid); System.out.println("Entered sessionId :"+
	 * sid); System.out.println("Entered userId :"+uid);
	 * System.out.println("Current Time in Millisecond "+session.tims);
	 * System.out.println("Encrypted SessionId :"+sessionObj.getString("sessionId"))
	 * ; String sessionId = sessionObj.getString("sessionId");
	 * System.out.println("---------------------------------------");
	 * System.out.println("sessionId pasing for Decryption is :"+sessionId);
	 * JSONObject decId = session.decryptSessionId(sessionId);
	 * System.out.println(decId.toString()); System.out.println("After decryption");
	 * System.out.println("SessionId :"+decId.getString("sessionId"));
	 * System.out.println("userId :"+decId.getString("userId"));
	 * System.out.println("Encrypted Time In MilliSecond "+decId.getString(
	 * "timeinmilliseconds")); }
	 */

	public SessionEntryption() {
		Calendar cal = Calendar.getInstance();
		Calendar baseday = Calendar.getInstance();
		baseday.set(Calendar.YEAR, startyear);
		baseday.set(Calendar.MONTH, startmonth);
		baseday.set(Calendar.DAY_OF_MONTH, startday);
		System.out.println("The diff " + cal.getTime() + "  " + baseday.getTime());
		timeInMilliseconds = (int) ((cal.getTimeInMillis() - baseday.getTimeInMillis()) / weekmillis);
		tims = timeInMilliseconds;
	}

	public JSONObject encryptSessionId(int incrementalID, int userID) {
		String encryptedSessionId = null;
		JSONObject obj = null;
		try {
			int base1 = incrementalID & 0x1F;
			int base2 = (incrementalID >> 5) & 0x1F;
			int base3 = (incrementalID >> 10) & 0x1F;
			int base4 = (incrementalID >> 15) & 0x1F;

			int base5 = userID & 0x1F;
			int base6 = (userID >> 5) & 0x1F;
			int base7 = (userID >> 10) & 0x1F;
			int base8 = (userID >> 15) & 0x1F;

			int base9 = timeInMilliseconds & 0x1F;
			int base10 = (timeInMilliseconds >> 5) & 0x1F;
			int base11 = (timeInMilliseconds >> 10) & 0x1F;
			int base12 = (timeInMilliseconds >> 15) & 0x1F;

			encryptedSessionId = "" + IDCHARS[base1] + IDCHARS[base5] + IDCHARS[base9] + IDCHARS[base2] + IDCHARS[base6]
					+ IDCHARS[base10] + IDCHARS[base3] + IDCHARS[base7] + IDCHARS[base11] + IDCHARS[base4]
					+ IDCHARS[base8] + IDCHARS[base12];
			obj = new JSONObject();
			obj.put("timeinmilliseconds", tims);
			obj.put("sessionId", encryptedSessionId);

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public JSONObject decryptSessionId(String encryptedSessionId) {
		int incrementalId = 0;
		int userId = 0;
		int timeInMilliseconds = 0;
		try {
			List<Integer> list = new ArrayList<Integer>();
			for (char ch : encryptedSessionId.toCharArray()) {
				int position = new String(IDCHARS).indexOf(ch);
				list.add(position);
			}
			int base1 = list.get(0);
			int base2 = list.get(3);
			int base3 = list.get(6);
			int base4 = list.get(9);

			int base5 = list.get(1);
			int base6 = list.get(4);
			int base7 = list.get(7);
			int base8 = list.get(10);

			int base9 = list.get(2);
			int base10 = list.get(5);
			int base11 = list.get(8);
			int base12 = list.get(11);

			incrementalId = incrementalId | base4;
			incrementalId = incrementalId << 5;
			incrementalId = incrementalId | base3;
			incrementalId = incrementalId << 5;
			incrementalId = incrementalId | base2;
			incrementalId = incrementalId << 5;
			incrementalId = incrementalId | base1;

			userId = userId | base8;
			userId = userId << 5;
			userId = userId | base7;
			userId = userId << 5;
			userId = userId | base6;
			userId = userId << 5;
			userId = userId | base5;

			timeInMilliseconds = timeInMilliseconds | base12;
			timeInMilliseconds = timeInMilliseconds << 5;
			timeInMilliseconds = timeInMilliseconds | base11;
			timeInMilliseconds = timeInMilliseconds << 5;
			timeInMilliseconds = timeInMilliseconds | base10;
			timeInMilliseconds = timeInMilliseconds << 5;
			timeInMilliseconds = timeInMilliseconds | base9;

			JSONObject Obj = new JSONObject();
			Obj.put("sessionId", incrementalId);
			Obj.put("userId", userId);
			Obj.put("timeinmilliseconds", timeInMilliseconds);
			System.out.println("Decrypted : " + Obj);
			return Obj;
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
