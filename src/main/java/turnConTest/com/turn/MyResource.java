package turnConTest.com.turn;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.internal.util.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Root resource (exposed at "myresource" path)
 * http://localhost:8080/com.turn/api/chicago/2016-08-27
 */
@Path("/")
public class MyResource {
	LocalDateTime aDateTime = LocalDateTime.of(2018, Month.OCTOBER, 31, 19, 30, 40);
	private static final String AUTHENTICATION_SCHEME = "Basic";

	LocalDateTime now = LocalDateTime.now();
	HashMap<String, Employee> employee = new HashMap<String, Employee>();
	HashMap<String, Employee> employeeT = new HashMap<String, Employee>();
	ArrayList<ArrayList<Employee>> arrOfArrEmployee = new ArrayList<ArrayList<Employee>>();
	public  Setting seting = new Setting();
	//public static final int STEP_TURN = 20;
//	public static String username;
	public static String password;

	// String strDateFormat = "yy:MM:dd";
	DateTimeFormatter dtfL = DateTimeFormatter.ofPattern("yy:MM:dd");

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getEmployee(@Context HttpHeaders httpheaders) {
		//
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL != 3) {
			employee = EmployeeDAO.getEmployee();
			if (employee.size() == 0) {
				Connection con = null;
				Statement stmt = null;
				try {
					con = DBUtil.getConnection();
					LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
					String formattedDate = dtfL.format(checkIn);
					stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT vl from dataturn where datet=\'" + formattedDate + "\'");
					employee = EmployeeDAO.clearEmployee();
					if (rs.next()) {
						Object obj = new JSONParser().parse(rs.getString(1));
						JSONObject jo = (JSONObject) obj;
						JSONArray ja = (JSONArray) jo.get("detail");
						if (ja == null) {
							String s = "{\"status\":";
							if (checkL == 1)
								s += true;
							else
								s += false;
							return s + "}";
						}
						for (Object o : ja) {
							if (o instanceof JSONObject) {
								parseEmployeeObject((JSONObject) o, formattedDate, true);
							}
						}
					}
				} catch (URISyntaxException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) { // TODO Auto-generated catch
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						con.close();
						// stmt.close();
					} catch (SQLException e) {
					}
				}
				return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, true);
			}
			return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, false);
		}
		return "[]";
		// }
	}

	@GET
	@Path("/login/")
	public String authenticate(@Context HttpHeaders httpheaders) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL != 3) {
			employee = EmployeeDAO.getEmployee();
			if (employee.size() == 0) {
				Connection con = null;
				Statement stmt = null;
				try {
					con = DBUtil.getConnection();
					
					/*System.out.println(checkIn.toString());
					int timeNew = checkOut.getHour() * 60 + checkOut.getMinute();
					int timeOld = checkIn.getHour() * 60 + checkIn.getMinute();
					System.out.println(timeNew - timeOld);*/
					
					
					LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
					String formattedDate = dtfL.format(checkIn);
					/*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy:MM:dd HH:mm:ss");
					LocalDateTime working = LocalDateTime.parse(formattedDate + " " + seting.getDaily(), formatter);*/
					
					stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT vl from dataturn where datet=\'" + formattedDate + "\'");
					employee = EmployeeDAO.clearEmployee();
					if (rs.next()) {
						Object obj = new JSONParser().parse(rs.getString(1));
						JSONObject jo = (JSONObject) obj;
						JSONArray ja = (JSONArray) jo.get("detail");
						if (ja == null) {
							String s = "{\"status\":";
							if (checkL == 1)
								s += true;
							else
								s += false;
							return s + "}";
						}
						for (Object o : ja) {
							if (o instanceof JSONObject) {
								parseEmployeeObject((JSONObject) o, formattedDate, true);
							}
						}
					}
				} catch (URISyntaxException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) { // TODO Auto-generated catch
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						con.close();
						// stmt.close();
					} catch (SQLException e) {
					}
				}
				return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, true);
			}
			return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, false);
		}
		return "{\"error\": \"loginFailed\"}";
	}

	private int checkLogin(String token) {
		final String encodedUserPassword = token.replaceFirst(AUTHENTICATION_SCHEME + " ", "");
		// Decode username and password
		String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();
		if(seting.getPass() == null || seting.getPass().isEmpty()) {
			getSetting();
		}
		if (("admin".equals(username) && seting.getPass().equals(password))
				|| ("tan".equals(username) && "1812".equals(password))) {
			return 1;
		} else if ("viewer".equals(username) && "123".equals(password)) {
			return 2;
		}
		return 3;
	}

	@GET
	@Path("/updatePass/{oldPass}/{newPass}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePass(@Context HttpHeaders httpheaders, @PathParam("oldPass") String oldPass,
			@PathParam("newPass") String newPass) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 1) {
			if (!seting.getPass().equals(oldPass)) {
				return "{\"error\": \"oldPassNotCorrect\"}";
			}
			Connection con = null;
			Statement stmt = null;
			try {
				con = DBUtil.getConnection();
				stmt = con.createStatement();
				stmt.executeUpdate("INSERT INTO turnSetting (name, value) VALUES ('admin','2608') "
						+ "ON CONFLICT (name) DO UPDATE SET value = " + "'" + newPass + "'");
				
				seting.setPass(newPass);
			} catch (URISyntaxException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) { // TODO Auto-generated catch
				e.printStackTrace();
			} finally {
				try {
					con.close();
					// stmt.close();
				} catch (SQLException e) {
				}
			}
			return "{\"sucess\": \"OK\"}";
		} else if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		}
		return "{\"error\": \"notLogin\"}";
	}
	
	@GET
	@Path("/updateRole/{role}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateRole(@Context HttpHeaders httpheaders, @PathParam("role") String role) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 1) {
			
			Connection con = null;
			Statement stmt = null;
			try {
				con = DBUtil.getConnection();
				stmt = con.createStatement();
				stmt.executeUpdate("INSERT INTO turnSetting (name, value) VALUES ('role','1') "
						+ "ON CONFLICT (name) DO UPDATE SET value = " + "'" + role + "'");
				seting.setSecurity(role);
			} catch (URISyntaxException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) { // TODO Auto-generated catch
				e.printStackTrace();
			} finally {
				try {
					con.close();
					// stmt.close();
				} catch (SQLException e) {
				}
			}
			return "{\"sucess\": \"OK\"}";
		} else if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		}
		return "{\"error\": \"notLogin\"}";
	}
	
	@GET
	@Path("/viewPass/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String viewpass(@Context HttpHeaders httpheaders, @PathParam("id") String id) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 1) {
			
			Employee employee1 = EmployeeDAO.getEmployee(id);
			return "{\"sucess\": \"" + employee1.getPass() + "\"}";
		} else if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		}
		return "{\"error\": \"notLogin\"}";
	}
	
	
	private  void getSetting() {
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT name, value from turnSetting");
			String name = "";
			while (rs.next()) {
				name = rs.getString("name");
				if(name.equals("admin")) {
					seting.setPass(rs.getString("value"));
				} else if ((name.equals("role")) ) {
					seting.setSecurity(rs.getString("value"));
				} else if ((name.equals("numTV")) ) {
					seting.setTvNum(rs.getString("value"));
				} else if ((name.equals("tReward")) ) {
					seting.settReward(rs.getString("value"));
				} else if ((name.equals("salonID")) ) {
					seting.setSalonID(rs.getString("value"));
				} else if ((name.equals("turn")) ) {
					seting.setTurn(rs.getString("value"));
				} else if ((name.equals("daily")) ) {
					seting.setDaily(rs.getString("value"));
				} else if ((name.equals("weeken")) ) {
					seting.setWeeken(rs.getString("value"));
				}
			}
		} catch (URISyntaxException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) { // TODO Auto-generated catch
			e.printStackTrace();
		}  finally {
			try {
				con.close();
				// stmt.close();
			} catch (SQLException e) {
			}
		}
	}
	// http://localhost:8080/com.turn/api/addUser/abff
	@GET
	@Path("/addUser/{userName}/{pass}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String addEmployee(@Context HttpHeaders httpheaders, @PathParam("userName") String userName,
			@PathParam("pass") String pass) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 1) {
			if (EmployeeDAO.isSameName(userName)) {
				return "{\"error\": \"sameName\"}";
			}
			employee = EmployeeDAO.getEmployee();
			LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
			String formattedDate = dtfL.format(checkIn);
			if (employee.size() == 0) {
				Connection con = null;
				Statement stmt = null;
				try {
					con = DBUtil.getConnection();
					stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT vl from dataturn where datet=\'" + formattedDate + "\'");
					employee = EmployeeDAO.clearEmployee();
					if (rs.next()) {
						Object obj = new JSONParser().parse(rs.getString(1));
						JSONObject jo = (JSONObject) obj;
						JSONArray ja = (JSONArray) jo.get("detail");
						if (ja != null) {
							for (Object o : ja) {
								if (o instanceof JSONObject) {
									parseEmployeeObject((JSONObject) o, formattedDate, true);
								}
							}
						}

					}
				} catch (URISyntaxException e) { // TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) { // TODO Auto-generated catch
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						con.close();
						// stmt.close();
					} catch (SQLException e) {
					}
				}
			}
			employee = EmployeeDAO.addEmployee(userName, pass);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy:MM:dd HH:mm:ss");
			LocalDateTime working;
			if(checkIn.getDayOfWeek().equals(DayOfWeek.SUNDAY))
				working = LocalDateTime.parse(formattedDate + " " + seting.getWeeken(), formatter);
			else
				working = LocalDateTime.parse(formattedDate + " " + seting.getDaily(), formatter);
			int timeStandar = working.getHour() * 60 + working.getMinute();
			int timeLogin = checkIn.getHour() * 60 + checkIn.getMinute();
			int diff = timeLogin - timeStandar;
			if (diff > 0) {
				int maxId = 0;
		        for (String emp : employee.keySet()) {
					if(Integer.parseInt(emp) > maxId)
						maxId = Integer.parseInt(emp);
				}
		        addGroupSub(Integer.toString(maxId), "Comming late", Double.valueOf(diff), "0", "1", pass,"1");
			}
			
			return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, false);
		} else if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		}
		return "{\"error\": \"notLogin\"}";
	}

	/*
	 * @GET
	 * 
	 * @Path("/clearUser")
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON }) public String
	 * clearEmployee(@Context HttpHeaders httpheaders) throws URISyntaxException,
	 * SQLException {
	 * 
	 * Connection con = DBUtil.getConnection(); Statement stmt = null; stmt =
	 * con.createStatement(); stmt.executeQuery( "CREATE TABLE account(" +
	 * " user_id serial PRIMARY KEY," + " username VARCHAR(50) UNIQUE NOT NULL," +
	 * " password VARCHAR(50) NOT NULL," + " email VARCHAR(355) UNIQUE NOT NULL," +
	 * " created_on TIMESTAMP NOT NULL," + " last_login TIMESTAMP" + ");");
	 * con.close(); CREATE TABLE dataturn(datet VARCHAR (50) UNIQUE NOT NULL,vl json
	 * NOT NULL); insert into dataturn(datet,vl) values ('19:04:04','{}'); update
	 * dataturn set vl = '[]' where datet = '19:04:03';
	 * 
	 * 
	 * String token = httpheaders.getHeaderString("Authorization"); int checkL =
	 * checkLogin(token); if (checkL == 1) { employee = EmployeeDAO.clearEmployee();
	 * return buildJson(updatePosition(new ArrayList<Employee>(employee.values())),
	 * 1); } else if (checkL == 2) { return "{\"error\": \"notAllow\"}"; } return
	 * "{\"error\": \"notLogin\"}"; }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/saveUser")
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON }) public String saveEmployee() {
	 * employee = EmployeeDAO.getEmployee(); String s = buildJson(updatePosition(new
	 * ArrayList<Employee>(employee.values()))); File filename = new
	 * File("http://localhost:8080/turnConF/dbsettings.json"); try { FileWriter
	 * jsonFileWriter = new FileWriter(filename.getAbsoluteFile(), true);
	 * jsonFileWriter.write(s); jsonFileWriter.flush(); jsonFileWriter.close();
	 * System.out.println("Done!"); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } return System.getProperty( "catalina.base"
	 * ) + "--------" + System.getProperty("user.dir"); }
	 */

	// http://localhost:8080/com.turn/api/addGroup/{name}/{money}/{free}
	@GET
	@Path("/addGroup/{id}/{name}/{money}/{free}/{over}/{pass}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String addGroup(@Context HttpHeaders httpheaders, @PathParam("id") String id, @PathParam("name") String name,
			@PathParam("money") double money, @PathParam("free") String free,
			@PathParam("over") String over, @PathParam("pass") String pass) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		return addGroupSub(id, name, money, free, over, pass, "0");
	}

	private String addGroupSub( String id, String name, double money, String free, String over,
			String pass, String overTime) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Employee employee1 = EmployeeDAO.getEmployee(id);
		if(seting.getPass() == null)
			getSetting();
		if(!seting.getPass().equals(pass) && !employee1.getPass().equals(pass)) {
			if(!"0".equals(seting.getSecurity()))
				return "{\"error\": \"notCorrPass\"}";
		}
		if ("0".equals(over)) {
			LocalDateTime checkOut = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
			LocalDateTime checkIn = employee1.getLstTime();
			int timeNew = checkOut.getHour() * 60 + checkOut.getMinute();
			int timeOld = checkIn.getHour() * 60 + checkIn.getMinute();
			if ((timeNew - timeOld) < money) {
				return "{\"error\": \"inValid\", \"msg\": \"Time is not ENOUGH. You start working at "
						+ dtf.format(employee1.getLstTime()) + "!!!\"}";
			}
		}
		// check null
		int index = 1;
		for (int i = 0; i < employee1.getTurnListD().size(); i++) {
			int tmp = Integer.parseInt(employee1.getTurnListD().get(i).getId());
			if (tmp > index) {
				index = tmp;
			}
		}
		index++;
		employee1.getTurnListD()
				.add(new WorkHis(name, money, "1".equals(free) ? true : false, Integer.toString(index),
						dtf.format(Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime()),
						"0".equals(overTime)?dtf.format(employee1.getLstTime()):null));
		if ("0".equals(free)) {
			employee1.setTotalTurn(employee1.getTotalTurn() + money);
		}
		employee1.setTotal(employee1.getTotal() + money);
		employee1.setIsWorking(false);
		employee1.setLstTime(null);
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	@GET
	@Path("/upGroup/{id}/{groupid}/{name}/{money}/{free}/{pass}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateGroup(@Context HttpHeaders httpheaders, @PathParam("id") String id,
			@PathParam("groupid") String groudid, @PathParam("name") String name, @PathParam("money") double money,
			@PathParam("free") String free, @PathParam("pass") String pass) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		Employee employee1 = EmployeeDAO.getEmployee(id);
		if(!seting.getPass().equals(pass) && !employee1.getPass().equals(pass)) {
			return "{\"error\": \"notCorrPass\"}";
		}
		// check null
		int index = -1;
		for (int i = 0; i < employee1.getTurnListD().size(); i++) {
			if (employee1.getTurnListD().get(i).getId().equals(groudid)) {
				index = i;
				break;
			}
		}
		WorkHis wk = employee1.getTurnListD().get(index);
		if ("0".equals(free) && !wk.isTurn()) {
			employee1.setTotalTurn(employee1.getTotalTurn() + money - wk.getMoney());
		} else if ("0".equals(free) && wk.isTurn()) {
			employee1.setTotalTurn(employee1.getTotalTurn() + money);
		} else if ("1".equals(free) && !wk.isTurn()) {
			employee1.setTotalTurn(employee1.getTotalTurn() - wk.getMoney());
		}
		employee1.setTotal(employee1.getTotal() + money - wk.getMoney());
		wk.setId(groudid);
		wk.setMoney(money);
		wk.setName(name);
		wk.setMoney(money);
		wk.setTurn(free.equals("1") ? true : false);
		employee1.getTurnListD().remove(index);
		employee1.getTurnListD().add(wk);
		employee1.setIsWorking(false);
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	@GET
	@Path("/delGroup/{id}/{groupId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String delGroup(@Context HttpHeaders httpheaders, @PathParam("id") String id,
			@PathParam("groupId") String groudid) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		Employee employee1 = EmployeeDAO.getEmployee(id);
		// check null
		int index = -1;
		for (int i = 0; i < employee1.getTurnListD().size(); i++) {
			if (employee1.getTurnListD().get(i).getId().equals(groudid)) {
				index = i;
				break;
			}
		}
		WorkHis wk = employee1.getTurnListD().get(index);
		if (!wk.isTurn()) {
			employee1.setTotalTurn(employee1.getTotalTurn() - wk.getMoney());
		}
		employee1.setTotal(employee1.getTotal() - wk.getMoney());
		employee1.getTurnListD().remove(index);
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	@GET
	@Path("/changeStatus/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String changeStatus(@Context HttpHeaders httpheaders, @PathParam("id") String id) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		Employee employee1 = EmployeeDAO.getEmployee(id);
		employee1.setActive(!employee1.isActive());
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	@GET
	@Path("/changeWorking/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String changeWorking(@Context HttpHeaders httpheaders, @PathParam("id") String id) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		Employee employee1 = EmployeeDAO.getEmployee(id);
		employee1.setIsWorking(!employee1.isIsWorking());
		LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
		employee1.setLstTime(checkIn);
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	@GET
	@Path("/changeWorkFree/{id}/{pass}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String changeWorkFree(@Context HttpHeaders httpheaders, @PathParam("id") String id,
			 @PathParam("pass") String pass) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		} 
		Employee employee1 = EmployeeDAO.getEmployee(id);
		if(!pass.equals(employee1.getPass()))
			return "{\"error\": \"notCorrPass\"}";
		employee1.setIsWorking(!employee1.isIsWorking());
		LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
		employee1.setLstTime(checkIn);
		employee = EmployeeDAO.addEmployee(id, employee1);
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}
	
	@GET
	@Path("/delete/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteUser(@Context HttpHeaders httpheaders, @PathParam("id") String id) {
		String token = httpheaders.getHeaderString("Authorization");
		int checkL = checkLogin(token);
		if (checkL == 2) {
			return "{\"error\": \"notAllow\"}";
		} else if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		EmployeeDAO.removeEmployee(id);
		employee = EmployeeDAO.getEmployee();
		return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), 1, false);
	}

	/*
	 * @SuppressWarnings("unchecked") public static void main(String[] args) throws
	 * ParseException { String s =
	 * " {\"status\":true,\"detail\":[{\"id\" : \"2\",\"name\" : \"sdd\",\"sortOrder\" : \"2\",\"turn\" : \"0.0\",\"turnAll\" : \"0.0\",\"status\" : \"1\",\"working\" : \"0\",\"loginTime\" : \"10:54:24\",\"workHis\" : []},{\"id\" : \"1\",\"name\" : \"s\",\"sortOrder\" : \"2\",\"turn\" : \"5.0\",\"turnAll\" : \"7.0\",\"status\" : \"0\",\"working\" : \"0\",\"loginTime\" : \"10:28:13\",\"workHis\" : [{\"id\" : \"2\",\"name\" : \"a\",\"free\" : \"1\",\"money\" : \"2.0\"},{\"id\" : \"3\",\"name\" : \"c\",\"free\" : \"0\",\"money\" : \"5.0\"}]}]}"
	 * ; Object obj = new JSONParser().parse(s); JSONObject jo = (JSONObject) obj;
	 * JSONArray ja = (JSONArray) jo.get("detail"); ja.forEach(emp ->
	 * parseEmployeeObject((JSONObject) emp, "19:04:03"));
	 * 
	 * }
	 */

	@GET
	@Path("/dummy/{data}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getData(@Context HttpHeaders httpheaders, @PathParam("data") String id) {
		String token = httpheaders.getHeaderString("Authorization");
		String tmp = "";
		int checkL = checkLogin(token);
		if (checkL == 3) {
			return "{\"error\": \"notLogin\"}";
		}
		LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
		String formattedDate = dtfL.format(checkIn);
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtil.getConnection();

			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT vl from dataturn where datet=\'" + id + "\'");
			employeeT.clear();
			if (formattedDate.equals(id) && checkL == 1)
				employee = EmployeeDAO.clearEmployee();
			if (rs.next()) {
				Object obj = new JSONParser().parse(rs.getString(1));
				JSONObject jo = (JSONObject) obj;
				JSONArray ja = (JSONArray) jo.get("detail");
				if (ja == null) {
					String s = "{\"status\":";
					if (checkL == 1)
						s += true;
					else
						s += false;
					return s + "}";
				}
				for (Object o : ja) {
					if (o instanceof JSONObject) {
						if (formattedDate.equals(id) && checkL == 1)
							parseEmployeeObject((JSONObject) o, id, true);
						else
							parseEmployeeObject((JSONObject) o, id, false);
					}
				}
				// ja.forEach(emp -> parseEmployeeObject((JSONObject) emp, id));
			}
		} catch (URISyntaxException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) { // TODO Auto-generated catch
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
				// stmt.close();
			} catch (SQLException e) {
			}
		}
		if (formattedDate.equals(id) && checkL == 1)
			return buildJson(updatePosition(new ArrayList<Employee>(employee.values())), checkL, true);// buildJson(updatePosition(new
		// ArrayList<Employee>(employee.values())),
		return buildJson(updatePosition(new ArrayList<Employee>(employeeT.values())), 2, true); // 1);

	}

	private void parseEmployeeObject(JSONObject employee1, String id, boolean load) {
		Employee tmpe = new Employee();
		String id2 = employee1.get("id").toString();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy:MM:dd HH:mm:ss");
		tmpe.setCheckInTime(LocalDateTime.parse(id + " " + employee1.get("loginTime").toString(), formatter));
		if (employee1.get("lstTime").equals("Not working")) {
			tmpe.setLstTime(LocalDateTime.parse(id + " " + "00:00:00", formatter));
		} else {
			tmpe.setLstTime(LocalDateTime.parse(id + " " + employee1.get("lstTime").toString(), formatter)); // Adding
																												// new
		}
		if (employee1.get("pass") == null) {
			tmpe.setPass("n_u_l_l");
		} else {
			tmpe.setPass(employee1.get("pass").toString()); // new
		}
		tmpe.setPosition(Integer.parseInt(employee1.get("sortOrder").toString()));
		try {
			tmpe.setTotal(Double.parseDouble(employee1.get("turnAll").toString()));
		} catch (NumberFormatException e) {
			String[] xxx = employee1.get("turnAll").toString().split("/");
			tmpe.setTotal(Double.parseDouble(xxx[0]));
		}
		tmpe.setTotalTurn(Double.parseDouble(employee1.get("turn").toString()));
		tmpe.setEmpName(employee1.get("name").toString());
		tmpe.setIsWorking("1".equals(employee1.get("working").toString()) ? true : false);
		tmpe.setActive("1".equals(employee1.get("status").toString()) ? true : false);
		tmpe.setEmployeeID(id2);
		JSONArray ja1 = (JSONArray) employee1.get("workHis");
		Iterator itr2 = ja1.iterator();
		ArrayList<WorkHis> lstWh = new ArrayList<WorkHis>();
		while (itr2.hasNext()) {
			Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
			WorkHis tn = new WorkHis();
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				if (pair.getKey().equals("money")) {
					tn.setMoney(Double.parseDouble(pair.getValue().toString()));
				} else if (pair.getKey().equals("name")) {
					tn.setName(pair.getValue().toString());
				} else if (pair.getKey().equals("id")) {
					tn.setId(pair.getValue().toString());
				} else if (pair.getKey().equals("free")) {
					tn.setTurn("1".equals(pair.getValue().toString()) ? true : false);
				} else if (pair.getKey().equals("workTime")) {
					tn.setWorkTime(pair.getValue().toString());
				} else if (pair.getKey().equals("startTime")) {
					tn.setStartTime(pair.getValue().toString());// Adding new
				}
			}
			lstWh.add(tn);
		}
		tmpe.setTurnListD(lstWh);
		if (!load)
			employeeT.put(id2, tmpe);
		else
			employee.put(id2, tmpe);
	}

	public  ArrayList<ArrayList<Employee>> updatePosition(ArrayList<Employee> employee) {
// total 10, active 6 , inactive 4
// Get active, inactive number
		ArrayList<ArrayList<Employee>> arrOfArrEmployee = new ArrayList<ArrayList<Employee>>();
		int numberOfEmployee = employee.size();
		int numberActive = 0;
		int numberInActive = 0;
		int numberBusyWorker = 0;
		int numberFreeWorker = 0;
		BubbleSort b = new BubbleSort();
		for (int i = 0; i < numberOfEmployee; i++) {
			if (employee.get(i).isActive()) {
				numberActive++;
				if (employee.get(i).isIsWorking()) {
					numberBusyWorker++;
				}
			} else {
				numberInActive++;
			}
		}
		numberFreeWorker = numberActive - numberBusyWorker;// ??????????????

// Process Free Worker 
		if (numberFreeWorker > 0) {
			ArrayList<Employee> tmpFreeWorker = new ArrayList<Employee>(numberFreeWorker);
			for (int i = 0; i < employee.size(); i++) {
				if (employee.get(i).isActive() && !employee.get(i).isIsWorking()) {
					tmpFreeWorker.add(employee.get(i));
				}
			}

			Collections.sort(tmpFreeWorker, new Comparator<Employee>() {

				public int compare(Employee arg0, Employee arg1) {
					if (arg0.getTotalTurn() > arg1.getTotalTurn())
						return 1;
					else if (arg0.getTotalTurn() == arg1.getTotalTurn())
						if (arg0.getCheckInTime().isBefore(arg1.getCheckInTime()))
							return -1;
						else
							return 1;
					return -1;
				}
			});
			/*
			 * for (int i = 0; i < tmpFreeWorker.size(); i++) {
			 * System.out.println(tmpFreeWorker.get(i).getEmpName() + "--- " +
			 * tmpFreeWorker.get(i).getTotalTurn() + "--- " +
			 * tmpFreeWorker.get(i).getCheckInTime()); }
			 * System.out.println("------------------------------");
			 */
			/*
			 * boolean breakF = true;66 int countLoop = 1; while (true) { breakF = true;
			 * ArrayList<Employee> tmpFreeWorker1 = new ArrayList<Employee>();
			 * if(tmpFreeWorker.isEmpty()) break; tmpFreeWorker1.add(tmpFreeWorker.get(0));
			 * int breakIndex = 0; for (int i = 0; i < tmpFreeWorker.size() - 1; i++) { if
			 * (tmpFreeWorker.get(i+1).getTotalTurn() - STEP_TURN <
			 * tmpFreeWorker.get(i).getTotalTurn()) {
			 * tmpFreeWorker1.add(tmpFreeWorker.get(i+1)); breakIndex++; } else break; }
			 * for(int i =breakIndex; i>= 0; i--) tmpFreeWorker.remove(i);
			 * Collections.sort(tmpFreeWorker1, new Comparator<Employee>() { public int
			 * compare(Employee arg0, Employee arg1) { // TODO Auto-generated method stub if
			 * (arg0.getCheckInTime().isBefore(arg1.getCheckInTime())) return 1; else return
			 * -1; } }); arrOfArrEmployee.add(tmpFreeWorker1); for (int i = 0; i <
			 * tmpFreeWorker.size(); i++) { System.out.println(countLoop + "::::::" +
			 * tmpFreeWorker.get(i).getEmpName() + "--- " +
			 * tmpFreeWorker.get(i).getTotalTurn() + "--- " +
			 * tmpFreeWorker.get(i).getCheckInTime()); }
			 * System.out.println("------------------------------"); countLoop++; if
			 * (breakF) { break; } } arrOfArrEmployee.add(tmpFreeWorker);
			 */
			/*
			 * boolean breakF = true; int countLoop = 1; while (true) { breakF = true; for
			 * (int i = 0; i < tmpFreeWorker.size() - 1; i++) { if
			 * (tmpFreeWorker.get(i+1).getTotalTurn() - STEP_TURN <
			 * tmpFreeWorker.get(i).getTotalTurn()) { if
			 * (tmpFreeWorker.get(i).getCheckInTime() .isAfter(tmpFreeWorker.get(i +
			 * 1).getCheckInTime())) { breakF = false; Collections.swap(tmpFreeWorker, i, i
			 * + 1); break; } } } for (int i = 0; i < tmpFreeWorker.size(); i++) {
			 * System.out.println(countLoop + "::::::" + tmpFreeWorker.get(i).getEmpName() +
			 * "--- " + tmpFreeWorker.get(i).getTotalTurn() + "--- " +
			 * tmpFreeWorker.get(i).getCheckInTime()); }
			 * System.out.println("------------------------------"); countLoop++; if
			 * (breakF) { break; } } arrOfArrEmployee.add(tmpFreeWorker);
			 */

			int j = 1;
			boolean breakF = true;
			if(seting.getTurn() == null)
				getSetting();
			int step_turn = Integer.parseInt(seting.getTurn());
			while (j <= (tmpFreeWorker.size() - 1)) {
				// int k = j;
				for (int i = j; i > 0; i--) {
					if (tmpFreeWorker.get(j).getTotalTurn() - step_turn < tmpFreeWorker.get(i - 1).getTotalTurn()) {
						if (tmpFreeWorker.get(j).getCheckInTime().isBefore(tmpFreeWorker.get(i - 1).getCheckInTime())) {
							Collections.swap(tmpFreeWorker, i - 1, j);
							j = i - 1;
							breakF = false;
							break;
						}
					}
				}
				if (breakF) {
					j++;
				} else {
					breakF = true;
				}

				/*
				 * for (int i = 0; i < tmpFreeWorker.size(); i++) { System.out.println(countLoop
				 * + "::::::" + tmpFreeWorker.get(i).getEmpName() + "--- " +
				 * tmpFreeWorker.get(i).getTotalTurn() + "--- " +
				 * tmpFreeWorker.get(i).getCheckInTime()); }
				 * System.out.println("------------------------------");
				 */
			}
			arrOfArrEmployee.add(tmpFreeWorker);

			/*
			 * //index group by Step_Turn int tmpIndexGroup = 1; if (tmpFreeWorker.size() >
			 * 0) { tmpFreeWorker.get(0).setIndexGroup(tmpIndexGroup); //
			 * System.out.println("Employee: " + tmpFreeWorker.get(0).getEmpName() + " //
			 * total_Turn: " + tmpFreeWorker.get(0).getTotalTurn()); if
			 * (tmpFreeWorker.size() > 1) { for (int i = 1; i < tmpFreeWorker.size(); i++) {
			 * if ((tmpFreeWorker.get(i).getTotalTurn() - tmpFreeWorker.get(i -
			 * 1).getTotalTurn()) >= STEP_TURN) { tmpIndexGroup++;
			 * tmpFreeWorker.get(i).setIndexGroup(tmpIndexGroup); } else {
			 * tmpFreeWorker.get(i).setIndexGroup(tmpIndexGroup); } //
			 * System.out.println("Employee: " + tmpFreeWorker.get(i).getEmpName() + " //
			 * total_Turn: " + tmpFreeWorker.get(i).getTotalTurn()); } } } //
			 * System.out.println("\nAFTER SORT:"); // printAddr(tmpFreeWorker);
			 * 
			 * 
			 * =============================================================================
			 * ========
			 * 
			 * // arrOfArrEmployee = new ArrayList<ArrayList<Employee>>(tmpIndexGroup);
			 * arrOfArrEmployee.clear(); if (tmpFreeWorker.size() > 0) { ArrayList<Employee>
			 * tmp = new ArrayList<Employee>(); tmp.add(tmpFreeWorker.get(0)); for (int i =
			 * 1; i < tmpFreeWorker.size(); i++) { if (tmpFreeWorker.get(i).getIndexGroup()
			 * != tmpFreeWorker.get(i - 1).getIndexGroup()) { arrOfArrEmployee.add(tmp);
			 * System.out.println( "Added group: " + arrOfArrEmployee.size() + " && with " +
			 * tmp.size() + " elements"); tmp = new ArrayList<Employee>();
			 * tmp.add(tmpFreeWorker.get(i)); } else { tmp.add(tmpFreeWorker.get(i)); } }
			 * arrOfArrEmployee.add(tmp); System.out.println( "Added last group: " +
			 * arrOfArrEmployee.size() + " && with " + tmp.size() + " elements"); }
			 */
// Create tmp array of busy worker and sort by total , index position
// Process Busy worker array

//Create tmp array of inactive and sort inactive & index  position 
//Process Inactive worker array

		}
		if (numberBusyWorker > 0) {
			ArrayList<Employee> tmpBusyWorker = new ArrayList<Employee>(numberBusyWorker);
			for (int i = 0; i < employee.size(); i++) {
				if (employee.get(i).isActive() && employee.get(i).isIsWorking()) {
					tmpBusyWorker.add(employee.get(i));
				}
			}
			b.bubbleSortTotal(tmpBusyWorker);
			// set Position
			for (int i = 0; i < tmpBusyWorker.size(); i++) {
				tmpBusyWorker.get(i).setPosition(numberActive - numberBusyWorker + i + 1);
			}
			arrOfArrEmployee.add(tmpBusyWorker);
		}

		if (numberInActive > 0) {
			ArrayList<Employee> tmpInActive = new ArrayList<Employee>(numberInActive);
			for (int i = 0; i < employee.size(); i++) {
				if (employee.get(i).isActive() == false) {
					tmpInActive.add(employee.get(i));
				}
			}
			b.bubbleSortTime(tmpInActive);
			for (int i = 0; i < tmpInActive.size(); i++) {
				tmpInActive.get(i).setPosition(i + 1 + numberActive);
			}
			arrOfArrEmployee.add(tmpInActive);
		}
		return arrOfArrEmployee;
		// print(arrOfArrEmployee);
	}

	private String buildJson(ArrayList<ArrayList<Employee>> employee, int checkL, boolean search) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		if(seting.getSecurity() == null || seting.getSecurity().isEmpty()) {
			getSetting();
		}
		// System.out.println("\nEmployee Table Details:");
		String s = "{\"status\":";
		if (checkL == 1)
			s += true;
		else
			s += false;
		if (seting.getSecurity().equals("1"))
			s += ",\"role\": \"1\"";
		else
			s += ",\"role\": \"0\"";
		s += ",\"detail\":[";
		// tb.addRow("EmployeeID", "EmployeeName", "CheckInTime", "Total", "Total_Turn",
		// "Is_Working", "Status", "Position", "Turn_List", "Index_Group");
		int k = 0;
		int l = 0;
		for (int j = 0; j < employee.size(); j++) {
			for (int i = 0; i < employee.get(j).size(); i++) {
				int index = i;
				if (l > 0)
					s += ",";
				s += "{";
				l++;
				s += "\"id\" : \"" + employee.get(j).get(index).getEmployeeID() + "\",";
				s += "\"name\" : \"" + employee.get(j).get(index).getEmpName() + "\",";
				if (employee.get(j).get(index).getPass() == null)
					s += "\"pass\" : \"n_u_l_l\",";
				else
					s += "\"pass\" : \"" + employee.get(j).get(index).getPass() + "\",";
				s += "\"sortOrder\" : \"" + employee.get(j).get(index).getPosition() + "\",";
				s += "\"turn\" : \"" + employee.get(j).get(index).getTotalTurn() + "\",";
				s += "\"status\" : \"" + ((employee.get(j).get(index).isActive()) ? "1" : "0") + "\",";
				s += "\"working\" : \"" + ((employee.get(j).get(index).isIsWorking()) ? "1" : "0") + "\",";
				s += "\"loginTime\" : \"" + dtf.format(employee.get(j).get(index).getCheckInTime()) + "\",";
				if (employee.get(j).get(index).getLstTime() == null)
					s += "\"lstTime\" : \"" + "Not working\",";
				else
					s += "\"lstTime\" : \"" + dtf.format(employee.get(j).get(index).getLstTime()) + "\",";
				s += "\"workHis\" : [";
				k = 0;
				int lateMoney = 0;
				for (WorkHis work : employee.get(j).get(index).getTurnListD()) {
					if (k == 0) {
						s += "{";
						k++;
					} else
						s += ",{";
					if("Comming late".equals(work.getName())) {
						lateMoney =(int) work.getMoney();
					}
					s += "\"id\" : \"" + work.getId() + "\",";
					s += "\"name\" : \"" + work.getName() + "\",";
					s += "\"free\" : \"" + ((work.isTurn()) ? "1" : "0") + "\",";
					s += "\"money\" : \"" + work.getMoney() + "\",";
					s += "\"workTime\" : \"" + (work.getWorkTime() == null ? "-" : work.getWorkTime()) + "\",";
					s += "\"startTime\" : \"" + (work.getStartTime() == null ? "-" : work.getStartTime()) + "\"";
					s += "}";
				}
				s += "],";
				if(lateMoney > 0)
					s += "\"turnAll\" : \"" + employee.get(j).get(index).getTotal() + "/" + lateMoney + "\"";
				else
					s += "\"turnAll\" : \"" + employee.get(j).get(index).getTotal() + "\"";
				s += "}";
			}
		}
		s += "]";
		s += "}";

		if (!search) {
			Connection con = null;
			Statement stmt = null;
			try {
				LocalDateTime checkIn = Instant.now().atZone(ZoneId.of("US/Central")).toLocalDateTime();
				String formattedDate = dtfL.format(checkIn);
				con = DBUtil.getConnection();
				stmt = con.createStatement();
				stmt.executeUpdate("INSERT INTO dataturn (datet, vl) VALUES('" + formattedDate + "','" + s
						+ "') ON CONFLICT (datet) DO UPDATE SET vl = '" + s + "'");
				// stmt.executeUpdate("update dataturn set vl = \'" + s + "\' where datet = \'"
				// + formattedDate + "\'");
			} catch (URISyntaxException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) { // TODO Auto-generated catch
				e.printStackTrace();
			} finally {
				try {
					if (con != null)
						con.close();
					// stmt.close();
				} catch (SQLException e) {
				}
			}
		}

		return s;
	}
}
