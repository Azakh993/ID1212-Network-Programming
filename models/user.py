from sqlalchemy import Column, INTEGER, VARCHAR
from sqlalchemy.orm import relationship

from controllers import Base


class User(Base):
    __tablename__ = 'users'

    id = Column(INTEGER, primary_key=True)
    username = Column(VARCHAR, unique=True, nullable=False)
    password = Column(VARCHAR, unique=False, nullable=False)

    registrations = relationship('UserCourseRegistration', back_populates='user')
