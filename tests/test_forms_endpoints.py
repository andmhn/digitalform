# return form response dto
from http.client import CREATED
from time import sleep
import requests
from requests.auth import HTTPBasicAuth
import uuid
import http
import json

from test_account_management import BASE_URL, headers, signup_payload, test_creation_of_new_user

formData = {
    "header": "Basic Details Form",
    "description": "Survey to know about society",
    "unlisted": False,
    "questions": [
    {
        "query": "What is Your Name?",
        "required": True,
        "type": "short_answer",
        "choices": None
    },
    {
        "query": "What is Your Age?",
        "required": False,
        "type": "number",
        "choices": None
    },
    {
        "query": "What is Your Gender?",
        "required": True,
        "type": "multiple_choice",
        "choices": [
        "male",
        "female",
        "trans"
        ]
    }
    ]
}

formResponse = {}

def test_creation_of_form():
    formPayload = json.dumps(formData)
        
    response = requests.post(
        BASE_URL + "/api/users/forms",
        headers= headers,
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"]),
        data = formPayload
    )

    assert response.status_code == http.HTTPStatus.CREATED
    global formResponse 
    formResponse = json.loads(response.content)
    assert formResponse['id'] != None
    assert formResponse['header'] == formData["header"]
    

def test_creation_of_form_without_credential():
    response = requests.post(
        BASE_URL + "/api/users/forms",
        headers= headers,
        data = json.dumps(formData)
    )
    assert response.status_code == http.HTTPStatus.UNAUTHORIZED
    

def test_get_form_by_id():
    response = requests.get(
        BASE_URL + "/api/public/forms?id=" + formResponse['id'],
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)['id'] == formResponse['id']
    assert json.loads(response.content) == formResponse
    
def test_getting_user_forms_data():
    response = requests.get(
        BASE_URL + "/api/users/forms/data",
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"]),
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)[0]['id'] == formResponse['id']
    assert json.loads(response.content)[0] == formResponse
    
    
def test_getting_user_forms_info():
    response = requests.get(
        BASE_URL + "/api/users/forms/info",
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"]),
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)[0]['id'] == formResponse['id']
    assert json.loads(response.content)[0] != formResponse
    

def test_get_public_forms_data_and_info():
    response = requests.get(
        BASE_URL + "/api/public/forms/data",
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    response = requests.get(
        BASE_URL + "/api/public/forms/info",
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK


# todo: delete all data being created here