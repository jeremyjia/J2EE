package com.pbz.demo.hello.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public final class JsonSriptParser {
	private static final String subtitle_video_name = "vSubtitle.mp4";
	private static final String final_video_name = "vFinal.mp4";
	private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

	public static boolean generateVideoByScriptFile(String scriptFilePath) throws Exception {
		String jsonString = new String(Files.readAllBytes(new File(scriptFilePath).toPath()));

		// Fix JSON string
		int s = jsonString.indexOf("{");
		if (s > 0) {
			jsonString = jsonString.substring(s);
			int e = jsonString.lastIndexOf("}");
			jsonString = jsonString.substring(s - 1, e + 1);
			jsonString = jsonString.replaceAll("\\\\", "");
			System.out.println("Fix for this input JSON string");
		}

		return generateVideo(jsonString);
	}

	private static boolean generateVideo(String jsonString) throws Exception {
		JSONObject jsonObj = new JSONObject(jsonString);
		JSONObject requestObj = getJsonObjectbyName(jsonObj, "request");

		String version = requestObj.getString("version");
		System.out.println("剧本版本:" + version);
		int width = requestObj.getInt("width");
		int height = requestObj.getInt("height");
		String audioFile = requestObj.getString("music");
		String rate = requestObj.getString("rate");
		int index = 0;

		Iterator<String> keys = requestObj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.equalsIgnoreCase("frames")) {
				JSONArray frameArray = (JSONArray) requestObj.get(key);
				for (Object frame : frameArray) {
					if (!(frame instanceof JSONObject)) {
						continue;
					}
					JSONObject frameObj = (JSONObject) frame;
					int times = frameObj.getInt("time");
					Color colorBackground = null;
					if (frameObj.has("backgroundColor")) {
						String color = frameObj.getString("backgroundColor");
						String[] colors = color.split(",");
						int red = Integer.parseInt(colors[0]);
						int green = Integer.parseInt(colors[1]);
						int blue = Integer.parseInt(colors[2]);
						colorBackground = new Color(red, green, blue);
					}
					Image bgImg = null;
					if (frameObj.has("backgroundPicture")) {
						String srcImageFile = frameObj.getString("backgroundPicture");
						File img = new File(srcImageFile);
						if (img.exists()) {
							bgImg = ImageIO.read(img);
						} else {
							System.out.println("WARNING: The file " + img.getName() + " doesn't exist!");
						}

					}
					for (int j = 0; j < times; j++) {
						String destImageFile = System.getProperty("user.dir") + "/" + Integer.toString(index + 1)
								+ ".jpg";
						BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						Graphics2D g = image.createGraphics();
						if (colorBackground != null) {
							g.setColor(colorBackground);
							g.fillRect(0, 0, width, height);
						}
						if (bgImg != null) {
							g.drawImage(bgImg, 0, 0, width, height, null);
						}

						JSONArray objectArray = frameObj.getJSONArray("objects");
						for (Object object : objectArray) {
							JSONObject obj = (JSONObject) object;
							// Picture
							if (obj.has("picture")) {
								String picFile = obj.getString("picture");
								File imgFile = new File(picFile);
								if (imgFile.exists()) {
									Image img = ImageIO.read(imgFile);
									int left = obj.getInt("x");
									int top = obj.getInt("y");
									int w = obj.getInt("width");
									int h = obj.getInt("heigth");
									g.drawImage(img, left, top, w, h, null);
								} else {
									System.out.println("WARNING: The file " + imgFile.getName() + " doesn't exist!");
								}
							}
							// Text
							if (obj.has("text")) {
								String text = obj.getString("text");
								int x = obj.getInt("x");
								int y = obj.getInt("y");
								int size = obj.getInt("size");
								String c = obj.getString("color");
								String[] co = c.split(",");
								int r = Integer.parseInt(co[0]);
								int gr = Integer.parseInt(co[1]);
								int b = Integer.parseInt(co[2]);
								System.out.println(text);
								g.setColor(new Color(r, gr, b));
								Font font = new Font("黑体", Font.BOLD, size);
								g.setFont(font);
								g.drawString(text, x, y);
							}
							//Graphic
							if(obj.has("graphic")) {
								drawGraphic(obj,g);
							}
						}
						g.setColor(new Color(0, 0, 255));// 帧号颜色
						g.setFont(new Font("黑体", Font.BOLD, 40));
						g.drawString(Integer.toString(index + 1), width - 100, 50);// 显示帧号
						g.dispose();
						ImageIO.write((BufferedImage) image, "JPEG", new File(destImageFile));
						index++;
					}
				}
			}
		}
		String suffix = isWindows ? ".bat" : ".sh";
		String cmd = System.getProperty("user.dir") + "/" + "jpg2video" + suffix;
		String[] args = { Integer.toString(width), Integer.toString(height), rate };
		ExecuteCommand.executeCommand(cmd, args);
		String ffmpegPath = "ffmpeg";
		if (!isWindows) {
			ffmpegPath = "/usr/bin/ffmpeg";
			if (!new File(ffmpegPath).exists()) {
				ffmpegPath = "/usr/local/bin/ffmpeg";
			}
		}
		// Cut the audio
		if (!new File(audioFile).exists()) {
			throw new Exception("The audio file " + audioFile + " doesn't exist!");
		}
		String tmpAudioFile = "tmpAudio.mp3";
		double dRate = Double.parseDouble(rate);
		long secondOfAudio = (long) ((double) index / dRate);
		secondOfAudio += 2;
		String endTime = milliSecondToTime(secondOfAudio * 1000);
		String[] cutAudioCmd = { ffmpegPath, "-y", "-i", audioFile, "-ss", "0:0:0", "-to", endTime, "-c", "copy",
				tmpAudioFile };
		ExecuteCommand.executeCommand(cutAudioCmd, null, new File("."), null);

		// Combine silent video and audio to final video
		String[] cmds = { ffmpegPath, "-y", "-i", subtitle_video_name, "-i", tmpAudioFile, final_video_name };
		boolean bRunScript = ExecuteCommand.executeCommand(cmds, null, new File("."), null);
		return bRunScript;
	}

	private static void drawGraphic(JSONObject jObj, Graphics2D gp2d) {
		String graphicType = jObj.getString("graphic");
		JSONObject attrObj = jObj.getJSONObject("attribute");
		int left = attrObj.getInt("left");
		int top = attrObj.getInt("top");
		String c = attrObj.getString("color");
		Color color = getColor(c);
		if ("line".equalsIgnoreCase(graphicType)) {
			int right = attrObj.getInt("right");
			int bottom = attrObj.getInt("bottom");
			if (attrObj.has("size")) {
				float fSize = attrObj.getFloat("size");
				BasicStroke bs = new BasicStroke(fSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
				gp2d.setStroke(bs);
			}
			gp2d.setColor(color);
			gp2d.drawLine(left, top, right, bottom);
		} else if ("circle".equalsIgnoreCase(graphicType)) {
			int width = attrObj.getInt("width");
			int height = attrObj.getInt("height");
			gp2d.setColor(color);
			gp2d.fillOval(left, top, width, height);
		} else if ("rect".equalsIgnoreCase(graphicType)) {
			int width = attrObj.getInt("width");
			int height = attrObj.getInt("height");
			gp2d.setColor(color);
			gp2d.fill3DRect(left, top, width, height, false);
		}
	}

	private static Color getColor(String color) {
		String[] colors = color.split(",");
		int red = Integer.parseInt(colors[0]);
		int green = Integer.parseInt(colors[1]);
		int blue = Integer.parseInt(colors[2]);
		return new Color(red, green, blue);
	}

	private static String milliSecondToTime(long millSecond) {
		long second = millSecond / 1000;
		second = second % 86400;
		long hours = second / 3600;
		second = second % 3600;
		long minutes = second / 60;
		second = second % 60;
		String s = String.format("%01d:%01d:%01d", hours, minutes, second);
		return s;
	}
	
	private static JSONObject getJsonObjectbyName(JSONObject jsonObj, String name) {
		if (jsonObj == null) {
			return null;
		}
		String[] names = JSONObject.getNames(jsonObj);
		if (names == null) {
			return null;
		}
		for (String s : names) {
			if (s.equalsIgnoreCase(name)) {
				return jsonObj.getJSONObject(s);
			}
		}
		return null;
	}

}
