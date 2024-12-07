import requests
from requests.auth import HTTPBasicAuth
import uuid
import http
import json


BASE_URL = "http://127.0.0.1:8080"

signup_payload = {
    "email"   : "test_user@mycompany.com" + uuid.uuid4().hex,
    "name"    : "test_user",
    "password": "test_password"
}

headers = {
    'Content-Type': 'application/json'
}


def test_creation_of_new_user():
    # requesting to create new user
    signup_response = requests.request("POST", BASE_URL + "/auth/signup", headers=headers, data=json.dumps(signup_payload))

    assert signup_response.status_code == http.HTTPStatus.CREATED


def test_authentication_of_new_user():
    # verify new user is on server
    auth_payload = {
        "email"   : signup_payload["email"],
        "password": signup_payload["password"]
    }
    auth_response = requests.request("POST",  BASE_URL + "/auth/authenticate", headers=headers, data=json.dumps(auth_payload))
    
    assert auth_response.status_code == http.HTTPStatus.OK


def test_bad_credential():
    auth_payload_1 = {
        "email"   : signup_payload["email"] + "bad",
        "password": signup_payload["password"]
    }
    auth_payload_2 = {
        "email"   : signup_payload["email"],
        "password": signup_payload["password"] + "bad"
    }
    
    auth_response_1 = requests.request("POST",  BASE_URL + "/auth/authenticate", headers=headers, data=json.dumps(auth_payload_1))
    assert auth_response_1.status_code == http.HTTPStatus.UNAUTHORIZED
    
    auth_response_2 = requests.request("POST",  BASE_URL + "/auth/authenticate", headers=headers, data=json.dumps(auth_payload_2))
    assert auth_response_2.status_code == http.HTTPStatus.UNAUTHORIZED


def delete_current_user() -> requests.Response:
    return requests.delete(
        BASE_URL + "/api/users/me",
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"])
    )


def test_deleting_user_as_a_USER():
    response = delete_current_user()
    assert response.status_code == http.HTTPStatus.OK
    
    # deleting again
    response = delete_current_user()
    assert response.status_code == http.HTTPStatus.UNAUTHORIZED