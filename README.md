# SpotFire

**Spotfire** is a crowd-sourcing tool that can support real-time detection and monitoring of wildfires; improving environmental safety to preserve it from wildfire risk.  
**Spotfire**, allows users to report wildfires with different means, and provides experts with the means to monitor them, gain insights, make predictions and give warnings to potential disasters.

This project was designed for the NASA Space Apps 2018 Challenge. For the 30 seconds pitch video and more details, please visit NASA Space Apps 2018 Challenge [Page](https://2018.spaceappschallenge.org/challenges/volcanoes-icebergs-and-asteroids-oh-my/real-time-fire-app/teams/google-it/project).

### Overview  

![Overview](https://images-2018.spaceappschallenge.org/stream-images/rkAkDMFHltgmsY2zuLQKSWIZdj8=/3472/width-800/)

# Getting Started

## Project Structure

```python
SPOFI
├── README.md #This File 
├── requirements.txt # Python dependencies
├── app # Android app
├── core # Backend, main service and database handlers 
    ├── data # All the open-data
    └── server.py # server side (one-method interface to handel reports submissions)
└── templates
	├── css
	├── js
	└──  platform.html # frontpage
```

## Installation

Develop on your own environment
First, clone the project
``` bash
$ git clone --recursive -j8 https://github.com/AhmedMaghawry/SPOFI.git
```

## Requirments
* Python 3.x
* Firebase
* Keras
* Tensorflow
* Pandas
* Numpy

Install all the prerequisites listed in requirements.txt by:
``` bash
$ pip install -r requirements.txt
```

Also, check the `README.md` file in the *core* folder.

## Contributers:
* [Ahmed Ezzat](https://github.com/AhmedMaghawry) 
* [Ahmed Rizk](https://github.com/AhmedMahmoudRizk) 
* [Youssef Ahmed](https://github.com/youssef-ahmed)
* [Yahia El-shahawy](https://github.com/yahia-elshahawy)
