import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import pandas as pd

# get report data part
def init_connection():
    cred = credentials.Certificate('spofi-service-key.json')
    firebase_admin.initialize_app(cred, {
        'databaseURL' : 'https://spofi-aacfa.firebaseio.com/'
    })
    root = db.reference()
    return root

def get_report_data(report_id):
    report_metadata = root.child('Reports').child(report_id).get()
    image_url= str(report_metadata['contentUrl'])
    lang, lat= float(report_metadata['location']['lang']),float(report_metadata['location']['lat'])
    date = str(report_metadata['date'])
    return image_url, lang, lat, date

def get_user_reports(report_id):
    users = root.child("Users")
    #print(users)
    user_report_ids = []
    for user_id in users.get():
        if(report_id in users.child(user_id).child('reportsId').get()):
            user_report_ids = list(map(str,users.child(user_id).child('reportsId').get()))
            specified_user_id = user_id
    data_dict = {'id': [], 'score': [], 'state': []}
    for report_id in user_report_ids:
        data_dict['id'].append(report_id)
        report = root.child("Reports").child(report_id)
        score = float(report.child('score').get())
        data_dict['score'].append(score)
        state = str(report.child('state').get())
        data_dict['state'].append(state)
    reports_info = pd.DataFrame(data_dict)
    print(reports_info)
    print(specified_user_id)
    return specified_user_id,reports_info


def update_trust(user_id,new_trust):
    root.child("Users").child(user_id).update({'rate' : new_trust})


def update_report_score(report_id, new_score, new_state):
    root.child("Reports").child(report_id).update({'score' : new_score,'state' : new_state})


root = init_connection()

