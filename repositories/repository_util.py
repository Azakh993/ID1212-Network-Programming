from repositories import session


def get_first_from_database(query):
    try:
        return query.first()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def get_all_from_database(query):
    try:
        return query.all()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def add_to_database(query):
    try:
        session.add(query)
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def delete_from_database(query):
    try:
        query.delete()
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
