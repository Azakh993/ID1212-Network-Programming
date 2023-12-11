from sqlalchemy import Column, INTEGER, VARCHAR, TIMESTAMP, ForeignKey
from sqlalchemy.orm import relationship

from controllers import Base


class BookingList(Base):
    __tablename__ = 'booking_lists'

    id = Column(INTEGER, primary_key=True, autoincrement=True)
    course_id = Column(VARCHAR, ForeignKey('courses.id'), nullable=False)
    description = Column(VARCHAR, nullable=False)
    location = Column(VARCHAR, nullable=False)
    time = Column(TIMESTAMP, nullable=False)
    interval = Column(INTEGER, nullable=False)
    max_slots = Column(INTEGER, nullable=False)

    course = relationship('Course', back_populates='booking_lists')
    reservations = relationship('Reservation', back_populates='booking_list')
