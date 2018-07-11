package com.dlri.chinacnr.bwts.quartz;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * @describe 读取FTP上的文件
 * @auto chuang chen
 * @date 2018-5-22 上午9:16:34
 */
public class FtpUtil {
	private static Logger logger = Logger.getLogger(FtpUtil.class);
	private FTPClient ftpClient = null;
	private String localPath = null; // 读取文件的存放目录
	private List<String> nameList;

	public FtpUtil() {

	}

	/**
	 * @param ip
	 * @param port
	 * @param userName
	 * @param userPwd
	 * @throws SocketException
	 * @throws IOException
	 *             function:连接到服务器
	 */
	public FtpUtil(String localPath, String ip, String port, String userName, String userPwd) {
		ftpClient = new FTPClient();
		try {
			// 连接
			// ftpClient.setControlEncoding("GBK");// 设置登陆编码格式
			// ftpClient.setConnectTimeout(60000);// 超时60秒
			ftpClient.connect(ip.trim(), Integer.parseInt(port));
			// 登录
			ftpClient.login(userName, userPwd);
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				try {
					throw new Exception("FTP  refuse connect");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			System.out.println("--->FTP连接超时！");
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IP 错误！");
		}
	}

	// String localPath,String ip, String port, String userName, String userPwd
	public boolean connectServer(String host, String port, String user, String password, String defaultPath) {
		localPath = defaultPath;
		ftpClient = new FTPClient();
		ftpClient.setDataTimeout(5000);
		ftpClient.setConnectTimeout(60);
		// ftpClient.setControlEncoding("UTF-8");
		try {
			ftpClient.connect(host.trim(), Integer.parseInt(port));
		} catch (NumberFormatException e) {
			System.out.println("==21=====");
			e.printStackTrace();
		} catch (SocketException e) {
			System.out.println(host.trim() + " 设备未开机，网络无法连接！");
			logger.error(host.trim() + " 设备未开机，网络无法连接！");
			// e.printStackTrace();
		} catch (IOException e) {
			// System.out.println("FTP服务器："+host+":"+port+" 连接超时！");
			// e.printStackTrace();
		}

		// log.info("Connected to " + host + ".");
		// log.info("FTP server reply code:" + ftpClient.getReplyCode());
		if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			try {
				if (ftpClient.login(user.trim(), password.trim())) {
					if (defaultPath != null && defaultPath.length() != 0) {
						ftpClient.changeWorkingDirectory(defaultPath);
					}

					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		closeServer();
		return false;
	}

	/**
	 * @throws IOException
	 *             function:关闭连接
	 */
	public void closeServer() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param fileName
	 *            function:删除文件
	 */
	public void deleteFile(String fileName) {
		try {
			ftpClient.deleteFile(new String(fileName.getBytes("GBK"), "iso-8859-1"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileName
	 *            function:删除文件夹
	 */
	public void removeDirectory(String fileName) {
		try {
			ftpClient.removeDirectory(new String(fileName.getBytes("GBK"), "iso-8859-1"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传输文件到集中的服务上后并删除原FTP服务器中的文件
	 * 
	 * @param str
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public List<String> transferAndDelFiles() {
		// 按日期创建文件夹
		Date date = new Date();
		String path = localPath + new SimpleDateFormat("yyyy/MM/dd").format(date);
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		nameList = new ArrayList<String>();
		String tempPath = "";
		ArrayList<String> pathArray = new ArrayList<String>();
		try {
			getPath(ftpClient, tempPath, pathArray);
			for (String string : pathArray) {
				string = new String(string.getBytes("iso-8859-1"), "GBK");
				ftpClient.changeWorkingDirectory(string);
				FTPFile[] file = ftpClient.listFiles();
				if (file.length != 0) {
					for (FTPFile ftpFile : file) {
						String originalFileName = new String(ftpFile.getName().getBytes("iso-8859-1"), "GBK");
						if (!ftpFile.isDirectory()) {
							String fileName = originalFileName.replace(" ", "-");
							String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
									.toLowerCase();
							fileName = fileName.substring(0, fileName.length() - 3) + fileType;
							if (fileType.equals("txt")) {
								nameList.add(path + "/" + fileName);
								System.out.println("-----传输的文件名称为：" + fileName);
							}

							File localFile = new File(path + "/" + fileName);
							// OutputStream is = new
							// FileOutputStream(localFile);
							// ftpClient.retrieveFile(ftpFile.getName(), is);
							// is.close();

							if (fileType.equals("txt") || fileType.equals("pdf") || fileType.equals("bgm")) {
								OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
								if (!ftpClient.retrieveFile(ftpFile.getName(), out)) {
									throw new IOException("Error loading file " + ftpFile.getName()
											+ " from FTP server. Check FTP permissions and path.");
								}
								out.flush();
								if (out != null) {
									try {
										out.close();
									} catch (IOException ex) {
									}
								}
								deleteFile(originalFileName);
							}
						}
					}
				} else {
					if (string.equals(".")) {
						// System.out.println("Ftp根目录不删除");
					} else {
						// removeDirectory(string);
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeServer();
		return nameList;
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public boolean deleteLocalFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

	public void getPath(FTPClient ftp, String path, ArrayList<String> pathArray) throws IOException {
		FTPFile[] files = ftp.listFiles();
		for (FTPFile ftpFile : files) {
			if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
				continue;
			if (ftpFile.isDirectory()) {// 如果是目录，则递归调用，查找里面所有文件
				path += "/" + ftpFile.getName();
				pathArray.add(path);
				ftp.changeWorkingDirectory(path);// 改变当前路径
				getPath(ftp, path, pathArray);// 递归调用
				path = path.substring(0, path.lastIndexOf("/"));// 避免对之后的同目录下的路径构造作出干扰，
			}
		}
		pathArray.add(".");
	}

	/*
	 * public List<String> download(String ftpFile, FTPClient ftp) {
	 * List<String> list = new ArrayList<String>(); String str = ""; InputStream
	 * is = null; BufferedReader br = null; try { // 获取ftp上的文件 is =
	 * ftp.retrieveFileStream(ftpFile); // 转为字节流 br = new BufferedReader(new
	 * InputStreamReader(is)); while ((str = br.readLine()) != null) {
	 * list.add(str); } br.close(); } catch (Exception e) { e.printStackTrace();
	 * } return list; }
	 * 
	 * public void download(FTPClient ftp, ArrayList<String> pathArray, String
	 * localRootPath) throws IOException { for (String string : pathArray) {
	 * String localPath = localRootPath + string; File localFile = new
	 * File(localPath); if (!localFile.exists()) { localFile.mkdirs(); } } for
	 * (String string : pathArray) { String localPath = localRootPath +
	 * string;// 构造本地路径 // ftp.changeWorkingDirectory(string); // FTPFile[] file
	 * = ftp.listFiles(); for (FTPFile ftpFile : file) { if
	 * (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
	 * continue; File localFile = new File(localPath); if
	 * (!ftpFile.isDirectory()) { OutputStream is = new
	 * FileOutputStream(localFile + "/" + ftpFile.getName());
	 * ftp.retrieveFile(ftpFile.getName(), is); is.close(); } } } }
	 */

	public void main(String[] args) throws ParseException, Exception {
		// FtpUtil ftp = new FtpUtil();
		/*
		 * try { ftp.transferAndDelFiles(); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
		String path = "";
		ArrayList<String> pathArray = new ArrayList<String>();
		getPath(ftpClient, path, pathArray);
		System.out.println(pathArray);
		// download(ftpClient, pathArray, "G:\\ftp1");

	}

}
