from sqlalchemy import Column, INTEGER, ForeignKey
from sqlalchemy.orm import relationship

from controllers import Base


class Reservation(Base):
    __tablename__ = 'reservations'

    id = Column(INTEGER, primary_key=True)
    list_id = Column(INTEGER, ForeignKey('booking_lists.id'), nullable=False)
    user_id = Column(INTEGER, ForeignKey('users.id'), nullable=False)
    sequence_id = Column(INTEGER, nullable=False)

    user = relationship('User', back_populates='reservations')
    booking_list = relationship('BookingList', back_populates='reservations')
