from sqlalchemy.orm import sessionmaker
from config.database import engine

Session = sessionmaker(bind=engine)
session = Session()
