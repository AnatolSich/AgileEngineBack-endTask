Tool execution should look like:

   java -jar Crawler.jar <input_origin_file_path> <target_element_id> <input_other_sample_file_path> 

For example:

    java -jar Crawler.jar samples/sample-0-origin.html make-everything-ok-button samples/sample-1-evil-gemini.html

The target element that needs to be found by default is a button with attribute id="make-everything-ok-button".
But definition custom id is possible.

 In diff-case page chooses the element with max attribute values matched. 
 If diff-case page has several elements with even matched attributes, than they all are added to result.

Output for sample pages:

html>body>div>div>div>div>div

html>body>div>div>div>div>div>div

html>body>div>div>div>div>div

html>body>div>div>div>div>div