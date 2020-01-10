#created by Osho Agyeya @IITKGP
from flask import Flask, render_template, request, flash,jsonify
import os
import numpy as np
import keras
from PIL import Image
import tensorflow as tf
from keras.models import load_model

app = Flask(__name__)
app.secret_key = "super secret key"  #when flash
APP_ROOT = os.path.dirname(os.path.abspath(__file__))

target = os.path.join(APP_ROOT, 'temp_img/')

model=None
graph=None

def my_model():
	global model
	model=load_model("/".join([APP_ROOT,'finetune_18.model']))
	global graph
	graph = tf.get_default_graph()

@app.route('/')
def index():
	return render_template('index.html')

@app.route("/upload", methods=['POST'])
def upload():
	if request.method=="POST":
		file = request.files["flow"]
		filename = file.filename
		destination = "/".join([target, filename])
		file.save(destination)
		ans=None
		img = Image.open(destination)
		input_shape = (224, 224)
		img_resized = img.resize(input_shape, Image.LANCZOS)
		img_array = np.expand_dims(np.array(img_resized), axis=0)
		global graph
		pred=None
		with graph.as_default():
			pred = model.predict_classes(img_array)
		if (pred[0] == 0):
			ans=("Rs.50")
		elif (pred[0] == 1):
			ans= ("Rs.500")
		elif (pred[0] == 2):
			ans=("Rs.100")
		elif (pred[0] == 3):
			ans= ("Rs.10")
		elif (pred[0] == 4):
			ans=("Rs.20")		
		os.remove(destination) 
		#K.clear_session()
		#flash(message1)
		return jsonify({'cash': ans})
		#return render_template('index.html')


if __name__ == "__main__":
	my_model()
	app.run(port=5000, debug=True)