import os

from dotenv import load_dotenv
from sqlalchemy import create_engine


load_dotenv()

DB = 'postgresql'
DB_USER = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_NAME = 'bookth'
DB_HOST = 'localhost'
DB_PORT = '5432'

sqlalchemy_db_uri = f'{DB}://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}'
engine = create_engine(sqlalchemy_db_uri)
