### Requirements

Some used models have large sizes so we couldn't upload them.

For the VGG16-places365 model, download it from:

http://places2.csail.mit.edu/

or from the repo:

https://github.com/GKalliatakis/Keras-VGG16-places365

Then save the model as `VGG16_Places365.hdf5`

The `serve.py` takes a request from tha client (app) to handle a new uploadd report, given its **report_id**.
The code is configured for a demo with **report_id** of and existing report in the firebase.
