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
submission_response_1 = []
submission_response_2 = []

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
    assert formResponse['form_id'] != None
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
        BASE_URL + "/api/public/forms?form_id=" + formResponse['form_id'],
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)['form_id'] == formResponse['form_id']
    assert json.loads(response.content) == formResponse
    
def test_getting_user_forms_data():
    response = requests.get(
        BASE_URL + "/api/users/forms/data",
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"]),
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)[0]['form_id'] == formResponse['form_id']
    assert json.loads(response.content)[0] == formResponse
    
    
def test_getting_user_forms_info():
    response = requests.get(
        BASE_URL + "/api/users/forms/info",
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"]),
        headers= headers
    )
    assert response.status_code == http.HTTPStatus.OK
    assert json.loads(response.content)[0]['form_id'] == formResponse['form_id']
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


def test_submission_of_form():
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/public/forms/submit?form_id=" + form_id
    submission = [
      {
        "question_id": formResponse['questions'][0]['question_id'],
        "answer": "Lana Del Rey"
      },
      {
        "question_id": formResponse['questions'][1]['question_id'],
        "answer": "39"
      },
      {
        "question_id": formResponse['questions'][2]['question_id'],
        "answer": "female"
      }
    ]
    response = requests.post(url, headers=headers, data=json.dumps(submission))

    assert response.status_code == http.HTTPStatus.ACCEPTED
    
    submissionResponse = json.loads(response.content)
    assert submissionResponse['submission_id'] != None
    assert submissionResponse['formId'] == form_id
    
    for i in range(len(submission)):
        assert submissionResponse['answers'][i]['answer_id'] != None
        assert submissionResponse['answers'][i]['question_id'] == submission[i]['question_id']
        assert submissionResponse['answers'][i]['answer'] == submission[i]['answer']
    global submission_response_1
    submission_response_1 = submissionResponse

  
def test_submission_of_form_with_only_required_answers():
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/public/forms/submit?form_id=" + form_id
    submission = [
      {
        "question_id": formResponse['questions'][0]['question_id'],
        "answer": "Lana Del Rey"
      },
      {
        "question_id": formResponse['questions'][2]['question_id'],
        "answer": "female"
      }
    ]
    response = requests.post(url, headers=headers, data=json.dumps(submission))
    assert response.status_code == http.HTTPStatus.ACCEPTED
    
    submissionResponse = json.loads(response.content)
    
    for i in range(0, 2, 2):
        assert submissionResponse['answers'][i]['answer_id'] != None
        assert submissionResponse['answers'][i]['question_id'] == submission[i]['question_id']
        assert submissionResponse['answers'][i]['answer'] == submission[i]['answer']
    global submission_response_2
    submission_response_2 = submissionResponse
  

def test_submission_of_form_without_required_answers():
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/public/forms/submit?form_id=" + form_id
    submission = [
      {
        "question_id": formResponse['questions'][0]['question_id'],
        "answer": "Lana Del Rey"
      },
      {
        "question_id": formResponse['questions'][1]['question_id'],
        "answer": "39"
      }
    ]
    response = requests.post(url, headers=headers, data=json.dumps(submission))

    assert response.status_code == http.HTTPStatus.BAD_REQUEST
    

def test_submission_of_form_with_additional_answer():
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/public/forms/submit?form_id=" + form_id
    submission = [
      {
        "question_id": formResponse['questions'][0]['question_id'],
        "answer": "Lana Del Rey"
      },
      {
        "question_id": formResponse['questions'][1]['question_id'],
        "answer": "39"
      },
      {
        "question_id": formResponse['questions'][2]['question_id'],
        "answer": "female"
      },
      {
        "question_id": 101,
        "answer": "redundant data"
      }
    ]
    response = requests.post(url, headers=headers, data=json.dumps(submission))

    assert response.status_code == http.HTTPStatus.BAD_REQUEST
    

def test_getting_submission_list_of_form():
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/users/forms/submissions?form_id=" + form_id

    response = requests.request("GET", 
                                url, 
                                headers=headers, 
                                auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"])
                                )
    submissions_list = json.loads(response.content)
    assert len(submissions_list) == 2
    assert submissions_list[0] == submission_response_1
    assert submissions_list[1] == submission_response_2


def test_should_fail_getting_submission_of_form_of_other_user():
    test_user = create_new_user()
    form_id = formResponse['form_id']
    url = BASE_URL + "/api/users/forms/submissions?form_id=" + form_id

    submission_response = requests.request(
        "GET", url, headers=headers, 
        auth=HTTPBasicAuth(test_user["email"], test_user["password"])
    )
    assert submission_response.status_code == http.HTTPStatus.FORBIDDEN
    delete_user(test_user["email"], test_user["password"])
 
   
def test_should_fail_getting_submission_list_of_non_existing_form():
    non_existing_form_id = str(uuid.uuid1())
    url = BASE_URL + "/api/users/forms/submissions?form_id=" + non_existing_form_id
    response = requests.request("GET", 
                                url, 
                                headers=headers, 
                                auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"])
                                )
    assert response.status_code == http.HTTPStatus.NOT_FOUND
  

def test_should_not_delete_submission_as_other_user():
    test_user = create_new_user()
    response = requests.delete(
        BASE_URL + "/api/users/forms/submit/" + str(submission_response_1['submission_id']),
        auth=HTTPBasicAuth(test_user["email"], test_user["password"])
    )
    assert response.status_code == http.HTTPStatus.UNAUTHORIZED


def test_should_delete_submission_as_form_owner():
    response = requests.delete(
        BASE_URL + "/api/users/forms/submit/" + str(submission_response_1['submission_id']),
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"])
    )
    assert response.status_code == http.HTTPStatus.OK
   

def test_should_not_delete_form_as_non_owner():
    test_user = create_new_user()
    response = requests.delete(
        BASE_URL + "/api/users/forms?form_id=" + formResponse['form_id'],
        auth=HTTPBasicAuth(test_user["email"], test_user["password"])
    )
    assert response.status_code == http.HTTPStatus.FORBIDDEN
    # delete user
    delete_user(test_user["email"], test_user["password"])
    

def test_should_delete_form_as_owner():
    response = requests.delete(
        BASE_URL + "/api/users/forms?form_id=" + formResponse['form_id'],
        auth=HTTPBasicAuth(signup_payload["email"], signup_payload["password"])
    )
    assert response.status_code == http.HTTPStatus.OK
    # check getting deleted form
    response = requests.get(
        BASE_URL + "/api/public/forms?form_id=" + formResponse['form_id']
    )
    assert response.status_code == http.HTTPStatus.NOT_FOUND
 

def test_delete_test_data_by_removing_user():
    delete_user(signup_payload["email"], signup_payload["password"])


def create_new_user() -> dict[str,str] :
    # create new test user
    test_user = {
        "email"   : "test_user@mycompany.com" + uuid.uuid4().hex,
        "name"    : "test_user",
        "password": "test_password" + uuid.uuid4().hex
    }
    response = requests.request(
        "POST", BASE_URL + "/auth/signup", headers=headers, data=json.dumps(test_user)
    )
    assert response.status_code == http.HTTPStatus.CREATED
    return test_user


def delete_user(email: str, password: str):
    response = requests.delete(
        BASE_URL + "/api/users/me",
            auth=HTTPBasicAuth(email, password)
    )
    assert response.status_code == http.HTTPStatus.OK