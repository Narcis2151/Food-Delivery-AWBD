create type order_status_enum as enum ('PENDING', 'PREPARING', 'READY', 'ACCEPTED', 'DELIVERING', 'COMPLETE', 'CANCELLED');


create table roles (
    id serial primary key,
    name varchar(50) unique not null
);

create table addresses (
    id serial primary key,
    street varchar(255) not null,
    city varchar(100) not null,
    state varchar(100) not null,
    country varchar(100) not null
);

create table users (
    id serial primary key,
    name varchar(255) not null,
    email varchar(255) unique not null,
    password varchar(255) not null,
    role_id int not null,
    phone_number varchar(20) not null,
    address_id int not null,
    foreign key (address_id) references addresses(id),
    foreign key (role_id) references roles (id)
);

create table stores (
    id serial primary key,
    name varchar(255) not null,
    address_id int not null,
    contact_phone_number varchar(20) not null,
    owner_id int not null,
    foreign key (address_id) references addresses(id),
    foreign key (owner_id) references users(id)
);

create table menu_item_categories (
    id serial primary key,
    store_id int not null,
    name varchar(255) not null,
    display_order int not null,

    foreign key (store_id) references stores(id)
);

create table menu_items (
    id serial primary key,
    name varchar(255) not null,
    description text,
    price decimal(10, 2) not null,
    is_available boolean not null,
    image_url varchar(255),

    category_id int not null,
    store_id int not null,
    foreign key (category_id) references menu_item_categories(id),
    foreign key (store_id) references stores(id)
);

create table orders (
    id serial primary key,
    user_id int not null,
    store_id int not null,
    order_status order_status_enum not null,
    total_amount decimal(10, 2) not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    foreign key (user_id) references users(id),
    foreign key (store_id) references stores(id)
);

create table order_items (
    id serial primary key,
    order_id int not null,
    menu_item_id int not null,
    quantity int not null,
    price decimal(10, 2) not null,
    foreign key (order_id) references orders(id),
    foreign key (menu_item_id) references menu_items(id)
);

create table reviews (
    id serial primary key,
    user_id int not null,
    menu_item_id int not null,
    rating int not null,
    comment text,
    created_at timestamp not null default current_timestamp,
    foreign key (user_id) references users(id),
    foreign key (menu_item_id) references menu_items(id)
);