Place the exported TensorFlow Lite models here.

Required files:
- detector.tflite
- classifier_head.tflite
- classifier_head_labels.txt
- classifier_chest.tflite
- classifier_chest_labels.txt
- classifier_arms.tflite
- classifier_arms_labels.txt
- classifier_waist.tflite
- classifier_waist_labels.txt
- classifier_legs.tflite
- classifier_legs_labels.txt

Expected label format per line:
<kind>__<armor-name-slug>__<armor-id>

Examples:
head__hope-mask__14
arms__chainmail-gloves__259

Current app behavior:
- If these files are missing, the camera screen stays usable and reports the missing asset paths.
- Once the files exist, only the TFLite interpreter implementation remains to replace the current mock inference branch in ArmorRecognitionRepositoryImpl.