import pandas as pd

data = pd.read_csv("data/FireData.csv") # path
data = data[data["confidence"]>95]

# get location score part
def get_potential_fire(lang,lat,R=1./60):  
    longs = data['longitude']
    lats = data['latitude']
    dx = longs - lang
    dy = lats - lat
    dsqr_sum = dx**2 + dy**2
    dsqr_sum = dsqr_sum[dsqr_sum <= R**2]
    d = [dsqr_sum.index]
    nearby_index = list(set(d[0]).intersection(*(d)))
    return len(nearby_index)

# TODO
def get_user_support_score(report_id):
	return 5
