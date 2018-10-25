import numpy as np
import matplotlib.pyplot as plt
import keras

from urllib.request import urlopen
from PIL import Image
from cv2 import resize


def get_classes():
    file_name = 'data/categories_places.txt'
    classes = []
    with open(file_name) as class_file:
        for line in class_file:
            classes.append(line.strip().split(' ')[0][3:])
    classes = tuple(classes)
    return classes



def preprocess_image(img):
    image = Image.open(urlopen(img))
    image = np.array(image, dtype=np.uint8)
    image = resize(image, (224, 224))
    image = np.expand_dims(image, 0)
    return image

def predict_scenes(image, top_pred=5):
    preds = model.predict(image)[0]
    top_preds = np.argsort(preds)[::-1][0:top_pred]
    classes = get_classes()
    labels = [classes[top_preds[i]] for i in range(0, top_pred)]
    return labels


def get_scene_score(img_url):
    image = preprocess_image(img_url)
    predictions = predict_scenes(image)
    #print("Predictions:")
    #print(predictions)
    matched_fields = 0
    for label in predictions:
        for word in important_words:
            if label.find(word) != -1:
                matched_fields +=1
    return matched_fields

#
model = keras.models.load_model('VGG16_Places365.hdf5')
# load classes names
classes = get_classes()
important_words = ["forest", "tree", "woods", "leaf", "leaves", "wild",
                "farm", "field", "landfill","corn_field","field/cultivated",
                "field/wild", "field_road", "hayfield"]
