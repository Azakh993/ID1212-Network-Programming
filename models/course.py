from sqlalchemy import Column, VARCHAR
from sqlalchemy.orm import relationship

from controllers import Base


class Course(Base):
    __tablename__ = 'courses'

    id = Column(VARCHAR, primary_key=True)
    name = Column(VARCHAR, nullable=False)

    registrations = relationship('UserCourseRegistration', back_populates='courses')
    booking_lists = relationship('BookingList', back_populates='courses')
