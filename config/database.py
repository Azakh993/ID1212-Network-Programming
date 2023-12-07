from sqlalchemy import create_engine

DB = 'postgresql'
DB_USER = 'adm1n'
DB_PASSWORD = 'dr0wssa9'
DB_NAME = 'python_quiz'
DB_HOST = 'localhost'
DB_PORT = '5432'

sqlalchemy_db_uri = f'{DB}://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}'
engine = create_engine(sqlalchemy_db_uri)
