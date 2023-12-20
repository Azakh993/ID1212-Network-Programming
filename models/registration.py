from sqlalchemy import Column, VARCHAR, INTEGER, ForeignKey, BOOLEAN

from controllers import Base


class UserCourseRegistration(Base):
    __tablename__ = 'user_course_registrations'

    user_id = Column(INTEGER, ForeignKey('users.id'), primary_key=True)
    course_id = Column(VARCHAR, ForeignKey('courses.id'), primary_key=True)
    admin = Column(BOOLEAN, nullable=False)
