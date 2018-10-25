import scene_validator
import location_validator
import database_handler
import fire_validator
import math

THRESHOLD = 0.80
w1, w2, w3, w4, w5, = 0.2, 0.2, 0.2, 0.2, 0.2

def sigmoid(x):
  return 1 / (1 + math.exp(-x))


def verify_user_report(report_id):
	image_url, lang, lat, date = database_handler.get_report_data(report_id)
	potential_fire_score = location_validator.get_potential_fire(lang, lat)
	users_support_score = location_validator.get_user_support_score(report_id)
	scene_score = scene_validator.get_scene_score(image_url)
	fire_score = fire_validator.get_fire_score(image_url)
	user_id, info = database_handler.get_user_reports(report_id)
	avg = info['score'].mean()
	verified = len(info[info['state']== 'Verified'])
	trust_rate = (verified/len(info)) * avg
	print(scene_score, potential_fire_score, users_support_score, users_support_score, fire_score, trust_rate)
	final_score = sigmoid(w1 * scene_score + w2* potential_fire_score + w3*users_support_score + w4*fire_score + w5*trust_rate)
	print(final_score)
	return final_score

def update_user_state(report_id):
	user_id, info = database_handler.get_user_reports(report_id)
	avg = info['score'].mean()
	verified = len(info[info['state']== 'Verified'])
	trust_rate = (verified/len(info)) * avg
	database_handler.update_trust(user_id, trust_rate*10)


def update_report_state(report_id):
	score = verify_user_report(report_id)
	if score >= THRESHOLD:
		database_handler.update_report_score(report_id, score, "Verified")
	else:
		database_handler.update_report_score(report_id, score, "Rejected")
	update_user_state(report_id)

