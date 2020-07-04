﻿var http = require('http');
var formidable = require('formidable');
var fs = require('fs');

console.log("__dirname = " + __dirname + Date() );

http.createServer(function (req, res) {
  if (req.url == '/fileupload') {
    var form = new formidable.IncomingForm();
    console.log(form);
    form.parse(req, function (err, fields, files) {
      var oldpath = files.filetoupload.path;
      var newpath = __dirname + '/' + files.filetoupload.name;
      fs.rename(oldpath, newpath, function (err) {
        if (err) 
	{
		console.log(err);
		throw err;
	}
        res.write('File uploaded and moved!');
        res.end();
      });
 });
  } else {
    res.writeHead(200, {'Content-Type': 'text/html'});
    res.write('<form action="fileupload" method="post" enctype="multipart/form-data">');
    res.write('<input type="file" name="filetoupload"><br>');
    res.write('<input type="submit">');
    res.write('</form>');
    return res.end();
  }
}).listen(8080);