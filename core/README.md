
### Task list
* [x] Creating Database 
* [x] Connecting to firebase, retrieving and applying updates
* [x] Testing Scene classification model
* [X] Testing Fire detection model
* [x] Building the *verification score* system
* [ ] Uploading service to a cloud
* [x] Testing report status update (e.g from pending to verified)
* [x] Testing user notification upon new fire incidents
* [ ] App sending direct requests to the cloud



### Requirements

Some used models have large sizes so we couldn't upload them.

**Fire detection**  
You can find details in:  
https://breckon.org/toby/publications/papers/dunni...

model download:  
https://github.com/tobybreckon/fire-detection-cnn  


**Scene classification**  
For the VGG16-places365 model, download it from:  
http://places2.csail.mit.edu/

or from the repo:  
https://github.com/GKalliatakis/Keras-VGG16-places365

Then save the model as `VGG16_Places365.hdf5`

The `serve.py` takes a request from tha client (the app) to handle a new uploaded report (associated with a **report_id**.  
The code is configured for a demo with a **report_id** of and existing report in the firebase.
