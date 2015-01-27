# Portfolio
This is an image manipulation and editing program. The original GUI framework to open, save, exit, and apply effects was given to me by Professor Calvin Lin.  
Graphics principles were used in its creation, including what to hide and display in layering and using matrix multiplication for translation and rotation. For rotation, 3 shearing matricies were used to prevent aliasing, which would cause holes in the rotated
image due to rounding of position. I found the logic here: http://datagenetics.com/blog/august32013/  
You can open an image, save an image, and exit from the File menu.From the Layer menu, you can open a new layer on top of the existing ones, select a different layer, and remove the selected layer. 
In the Edit menu, you can translate the selected layer vertically and horizontally, and rotate the layer from 0-360 degrees.  
The Effects menu applies various filters and effects to the layer, namely:  
Black and White - turns the layer black and white  
Blue Only - displays the layer in shades of blue  
Green Only - displays the layer in shades of green  
Grow - doubles the layer size  
Horizontal Reflect - reflects the layer about the horizontal axis  
Invert - invert the colors of the layer  
Red Only - displays the layer in shades of red  
Removes Blue - removes blue from the layer  
Removes Green - removes green from the layer  
Removes Red - removes red from the layer  
Shrink - halfs the layer size  
Smooth - smoothes the image by reducing its resolution  
Threshold - Reduces each value of RGB to either fully on or off, depending on given threshold (0-255)  
Vertical Reflect - reflects the layer about the vertical axis
