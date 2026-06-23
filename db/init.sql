SELECT 'CREATE DATABASE food_delivery_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'food_delivery_db')\gexec
