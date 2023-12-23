from sqlalchemy import Column, VARCHAR

from controllers import Base


class Course(Base):
    __tablename__ = 'courses'

    id = Column(VARCHAR, primary_key=True)
    name = Column(VARCHAR, nullable=False)
