CREATE TABLE Login (
  Username text PRIMARY KEY,
  Password text NOT NULL,
  User_type text NOT NULL,
  CONSTRAINT check_utype CHECK (User_type IN ('Admin', 'Employee', 'Customer'))
);

CREATE TABLE Employees (
  Username text PRIMARY KEY,
  First text NOT NULL,
  Last text NOT NULL,
  CONSTRAINT uname_fkey FOREIGN KEY (Username)
    REFERENCES Login (Username) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Customers (
  --customer_id int PRIMARY KEY, -- I think that customer_id needs to be an int. See getCustomerID
  Username text PRIMARY KEY,
  First text NOT NULL,
  Last text NOT NULL,
  Email text NOT NULL,
  Address text NOT NULL,
  CONSTRAINT uname_fkey FOREIGN KEY (Username)
  REFERENCES Login (Username) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT check_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
);

CREATE TABLE Cards (
  CC_num int PRIMARY KEY,
  Username text,
  Expiration date NOT NULL,
  CVV int NOT NULL,
  CONSTRAINT uname_fkey FOREIGN KEY (Username)
  REFERENCES Login (Username),
  CONSTRAINT check_cc CHECK (
    floor(log(CC_num)+1) = 16
    AND floor(log(CVV)+1) > 2
    AND floor(log(CVV)+1) < 5
  )
);

CREATE TABLE Rooms (
  Room_num int PRIMARY KEY,
  Facing text NOT NULL,
  Size text NOT NULL,
  CONSTRAINT check_room CHECK (
    Facing IN ('Ocean', 'Pool')
    AND Size IN ('Double Queen', 'Single King')
  )
);

CREATE TABLE Prices (
  Room_num int,
  Date date,
  Price real NOT NULL,
  PRIMARY KEY (Room_num, Date),
  CONSTRAINT rnum_fkey FOREIGN KEY (Room_num)
    REFERENCES Rooms (Room_num),
  CONSTRAINT check_price CHECK (price > 0)
);

CREATE TABLE Reservations (
  reservation_id serial PRIMARY KEY,
  Username text,
  Room_num int,
  Start_date date NOT NULL,
  End_date date NOT NULL,
  CONSTRAINT uname_fkey FOREIGN KEY (Username)
  REFERENCES Login (Username),
  CONSTRAINT rnum_fkey FOREIGN KEY (Room_num)
  REFERENCES Rooms (Room_num),
  CONSTRAINT check_dates CHECK (Start_date < End_date)
);

CREATE TABLE Transactions (
  transactions_id serial PRIMARY KEY,
  reservation_id int,
  Description text NOT NULL,
  Amount real NOT NULL,
  CONSTRAINT res_fkey FOREIGN KEY (reservation_id)
    REFERENCES Reservations (reservation_id),
  CONSTRAINT check_amount CHECK (Amount > 0)
);

INSERT into Login VALUES ('admin', 'admin', 'Admin');