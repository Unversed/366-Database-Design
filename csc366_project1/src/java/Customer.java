
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.util.Date;
import java.util.TimeZone;
import javax.el.ELContext;
import javax.faces.bean.ManagedProperty;

@Named(value = "customer")
@SessionScoped
@ManagedBean
public class Customer implements Serializable {

    @ManagedProperty(value = "#{login}")
    private Login login;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    @ManagedProperty(value = "#{card}")
    Card customerCreditCard;

    private DBConnect dbConnect = new DBConnect();

    //Shouldn't this be delegated to members of Login object?
    //I might be missing something here - Liam
    private String password;

    private String username;
    private String first;
    private String last;
    private String email;
    private String address;

    private Date created_date;

    private long cc_num;
    private byte month; //MM format for credit card expiration
    private short year;  //yyyy format for credit card expiration
    private short cvv;

    public String getCustomerUsername() throws SQLException {
        if (username == null) {
            Connection con = dbConnect.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            PreparedStatement ps
                    = con.prepareStatement(
                            "select ? from customers");
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                return null;
            }
            username = result.getString(1);
            result.close();
            con.close();
        }
        return username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst() {
        // ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        //Login login = (Login) elContext.getELResolver().getValue(elContext, null, "login");

        //  return login.getLogin();
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        this.created_date = created_date;
    }*/

    public long getCc_num() {
        return cc_num;
    }

    public void setCc_num(long cc_num) {
        this.cc_num = cc_num;
    }

    public byte getMonth() {
        return month;
    }

    public void setMonth(byte month) {
        this.month = month;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public short getCvv() {
        return cvv;
    }

    public void setCvv(short cvv) {
        this.cvv = cvv;
    }
    
    
    
    public String createCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Util.createLogin(username, password, "Customer", con);

        Util.createCard(cc_num, username, Card.getDate(year, month), cvv, con);

        Statement statement = con.createStatement();
        PreparedStatement preparedStatement = con.prepareStatement("Insert into customers values(?,?,?,?,?)");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, first);
        preparedStatement.setString(3, last);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, address);
        //preparedStatement.setDate(4, new java.sql.Date(created_date.getTime()));
        preparedStatement.executeUpdate();
        statement.close();
        con.commit();
        con.close();

        //Util.invalidateUserSession();
        return "main";
    }

    public String deleteCustomer() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        Util.deleteLogin(username, con);
        /*Statement statement = con.createStatement();
        statement.executeUpdate("Delete from customers where customer_id = " + customerID);
        statement.close();
        con.commit();*/
        con.close();
        Util.invalidateUserSession();
        return "main";
    }

    public String showCustomer() throws SQLException {
        getCustomer();
        return "showCustomer";
    }

    public Customer getCustomer() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select * from customers where username = ?");
        ps.setString(1, username);

        //get customer data from database
        ResultSet result = ps.executeQuery();

        result.next();

        username = result.getString("Username");
        first = result.getString("First");
        last = result.getString("Last");
        email = result.getString("email");
        address = result.getString("address");
        //created_date = result.getDate("created_date");
        return this;
    }

    public List<Customer> getCustomerList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps
                = con.prepareStatement(
                        "select Username, First, Last, Email, Address from customers");
        //date_created was in this query, but it's not in our database so I removed it but don't know why it was in this query

        //get customer data from database
        ResultSet result = ps.executeQuery();

        List<Customer> list = new ArrayList<Customer>();

        while (result.next()) {
            Customer cust = new Customer();

            cust.setUsername(result.getString("Username"));
            cust.setFirst(result.getString("First"));
            cust.setLast(result.getString("Last"));
            cust.setEmail(result.getString("Email"));
            cust.setAddress(result.getString("Address"));
            //cust.setCreated_date(result.getDate("created_date"));

            //store all data into a List
            list.add(cust);
        }
        result.close();
        con.close();
        return list;
    }

    public void customerUsernameExists(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {

        if (!existsCustomerUsername((String) value)) {
            FacesMessage errorMessage = new FacesMessage("Username does not exist");
            throw new ValidatorException(errorMessage);
        }
    }

    /*public void validateCustomerID(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        int id = (Integer) value;
        System.out.println("id:" + id);
        if (id < 0) {
            FacesMessage errorMessage = new FacesMessage("ID must be positive");
            throw new ValidatorException(errorMessage);
        }
        if (existsCustomerId((Integer) value)) {
            FacesMessage errorMessage = new FacesMessage("ID already exists");
            throw new ValidatorException(errorMessage);
        }
    }*/
    public void validateUsername(FacesContext context, UIComponent componentToValidate, Object value)
            throws ValidatorException, SQLException {
        String user = (String) value;

        System.out.println("user: " + user);
        if (user == null) {
            FacesMessage errorMessage = new FacesMessage("Username must be not null");
            throw new ValidatorException(errorMessage);
        }
        if (existsCustomerUsername(user)) {
            FacesMessage errorMessage = new FacesMessage("Username already exist");
            throw new ValidatorException(errorMessage);
        }
    }

    /*private boolean existsCustomerUsername(String username) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement ps = con.prepareStatement("select * from customers where username = " + username);

        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.close();
            return true;
        }
        result.close();
        con.close();
        return false;
    }*/
    private boolean existsCustomerUsername(String user) throws SQLException {
        Connection con = dbConnect.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        con.setAutoCommit(false);

        PreparedStatement ps = con.prepareStatement("select username from customers where username = ?");
        ps.setString(1, user);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.close();
            con.commit();
            con.close();
            return true;
        }
        result.close();
        con.commit();
        con.close();
        return false;
    }
}
