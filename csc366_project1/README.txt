List of relations
cards, customers, employees, login, prices, reservations, rooms, transactions 

User Types:  Admin, Employee, Customer
Room Size:   Double Queen, Single King
Room Facing: Ocean, Pool

Table "public.cards"
   Column   |  Type   | Modifiers
------------+---------+-----------
 cc_num     | integer | not null
 username   | text    |
 expiration | date    | not null
 cvv        | integer | not null
Indexes:
    "cards_pkey" PRIMARY KEY, btree (cc_num)
Check constraints:
    "check_cc" CHECK (floor(log(cc_num::double precision) + 1::double precision) = 16::double precision AND floor(log(cvv::double precision) + 1::double precision) > 2::double precision AND floor(log(cvv::double precision) + 1::double precision) < 5::double precision)
Foreign-key constraints:
    "uname_fkey" FOREIGN KEY (username) REFERENCES login(username)


Table "public.customers"

  Column  | Type | Modifiers
----------+------+-----------
 username | text | not null
 first    | text | not null
 last     | text | not null
 email    | text | not null
 address  | text | not null
Indexes:
    "customers_pkey" PRIMARY KEY, btree (username)
Check constraints:
    "check_email" CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'::text)
Foreign-key constraints:
    "uname_fkey" FOREIGN KEY (username) REFERENCES login(username) ON UPDATE CASCADE ON DELETE CASCADE


Table "public.employees"

  Column  | Type | Modifiers
----------+------+-----------
 username | text | not null
 first    | text | not null
 last     | text | not null
Indexes:
    "employees_pkey" PRIMARY KEY, btree (username)
Foreign-key constraints:
    "uname_fkey" FOREIGN KEY (username) REFERENCES login(username) ON UPDATE CASCADE ON DELETE CASCADE


Table "public.login"

  Column   | Type | Modifiers
-----------+------+-----------
 username  | text | not null
 password  | text | not null
 user_type | text | not null
Indexes:
    "login_pkey" PRIMARY KEY, btree (username)
Check constraints:
    "check_utype" CHECK (user_type = ANY (ARRAY['Admin'::text, 'Employee'::text, 'Customer'::text]))
Referenced by:
    TABLE "cards" CONSTRAINT "uname_fkey" FOREIGN KEY (username) REFERENCES login(username)
    TABLE "reservations" CONSTRAINT "uname_fkey" FOREIGN KEY (username) REFERENCES login(username)
    TABLE "customers" CONSTRAINT "uname_fkey" FOREIGN KEY (username) REFERENCES login(username) ON UPDATE CASCADE ON DELETE CASCADE
    TABLE "employees" CONSTRAINT "uname_fkey" FOREIGN KEY (username) REFERENCES login(username) ON UPDATE CASCADE ON DELETE CASCADE


Table "public.prices"

  Column  |  Type   | Modifiers
----------+---------+-----------
 room_num | integer | not null
 date     | date    | not null
 price    | real    | not null
Indexes:
    "prices_pkey" PRIMARY KEY, btree (room_num, date)
Check constraints:
    "check_price" CHECK (price > 0::double precision)
Foreign-key constraints:
    "rnum_fkey" FOREIGN KEY (room_num) REFERENCES rooms(room_num)

	
Table "public.reservations"

     Column     |  Type   |                               Modifiers
----------------+---------+-----------------------------------------------------------------------
 reservation_id | integer | not null default nextval('reservations_reservation_id_seq'::regclass)
 username       | text    |
 room_num       | integer |
 start_date     | date    | not null
 end_date       | date    | not null
Indexes:
    "reservations_pkey" PRIMARY KEY, btree (reservation_id)
Check constraints:
    "check_dates" CHECK (start_date < end_date)
Foreign-key constraints:
    "rnum_fkey" FOREIGN KEY (room_num) REFERENCES rooms(room_num)
    "uname_fkey" FOREIGN KEY (username) REFERENCES login(username)
Referenced by:
    TABLE "transactions" CONSTRAINT "res_fkey" FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)

Table "public.rooms"

  Column  |  Type   | Modifiers
----------+---------+-----------
 room_num | integer | not null
 facing   | text    | not null
 size     | text    | not null
Indexes:
    "rooms_pkey" PRIMARY KEY, btree (room_num)
Check constraints:
    "check_room" CHECK ((facing = ANY (ARRAY['Ocean'::text, 'Pool'::text])) AND (size = ANY (ARRAY['Double Queen'::text, 'Single King'::text])))
Referenced by:
    TABLE "prices" CONSTRAINT "rnum_fkey" FOREIGN KEY (room_num) REFERENCES rooms(room_num)
    TABLE "reservations" CONSTRAINT "rnum_fkey" FOREIGN KEY (room_num) REFERENCES rooms(room_num)


Table "public.transactions"

	Column       |  Type   |                               Modifiers
-----------------+---------+------------------------------------------------------------------------
 transactions_id | integer | not null default nextval('transactions_transactions_id_seq'::regclass)
 reservation_id  | integer |
 description     | text    | not null
 amount          | real    | not null
Indexes:
    "transactions_pkey" PRIMARY KEY, btree (transactions_id)
Check constraints:
    "check_amount" CHECK (amount > 0::double precision)
Foreign-key constraints:
    "res_fkey" FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)

Success