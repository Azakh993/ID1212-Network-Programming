"""
Database Repository Utilities

This module contains utility functions for interacting with the database using SQLAlchemy queries.
It includes functions to retrieve data from the database, add objects to the database,
and delete objects from the database.

"""

from repositories import session


def get_first_from_database(query):
    """
    Retrieve the first result from the database query.

    Parameters
    ----------
    query : Query
        The SQLAlchemy query object.

    Returns
    -------
    object or None
        The first result from the query, or None if no result is found.

    Raises
    ------
    Exception
        If an error occurs during the database operation, it is caught, and a rollback is performed.

    """

    try:
        return query.first()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def get_all_from_database(query):
    """
    Retrieve all results from the database query.

    Parameters
    ----------
    query : Query
        The SQLAlchemy query object.

    Returns
    -------
    list
        A list of objects retrieved from the query.

    Raises
    ------
    Exception
        If an error occurs during the database operation, it is caught, and a rollback is performed.

    """

    try:
        return query.all()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def add_to_database(query):
    """
    Add an object to the database.

    Parameters
    ----------
    query : Query
        The object to be added to the database.

    Raises
    ------
    Exception
        If an error occurs during the database operation, it is caught, and a rollback is performed.

    """

    try:
        session.add(query)
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise


def delete_from_database(query):
    """
    Delete an object from the database.

    Parameters
    ----------
    query : Query
        The object to be deleted from the database.

    Raises
    ------
    Exception
        If an error occurs during the database operation, it is caught, and a rollback is performed.

    """

    try:
        query.delete()
        session.commit()
    except Exception as exception:
        print(f'Error: {str(exception)}')
        session.rollback()
        raise
