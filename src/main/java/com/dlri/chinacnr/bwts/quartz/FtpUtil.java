package com.dlri.chinacnr.bwts.quartz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @describe 读取FTP上的文件
 * @auto li.wang
 * @date 2013-11-18 下午4:07:34
 */
public class FtpUtil {

	private  FTPClient ftpClient=null;
	private  String localPath = null; // 读取文件的存放目录
	public FtpUtil() {
		
	}

	/**
	 * @param ip
	 * @param port
	 * @param userName
	 * @param userPwd
	 * @throws SocketException
	 * @throws IOException
	 * function:连接到服务器
	 */
	public  FtpUtil(String localPath,String ip, String port, String userName, String userPwd) {
		//this.localPath=localPath;
		ftpClient = new FTPClient();
		try {
			// 连接
			//System.out.println("连接======用户信息======"+userName+"======"+userPwd+"==="+ip+"==="+port);
			//ftpClient.setControlEncoding("GBK");// 设置登陆编码格式
			//ftpClient.setConnectTimeout(60000);// 超时60秒
			ftpClient.connect(ip.trim(), Integer.parseInt(port));
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
          //      this.closeServer();
                try {
					throw new Exception("FTP  refuse connect");
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
			// 登录
			
			ftpClient.login(userName, userPwd);
		} catch (SocketException e) {
			System.out.println("--->FTP连接超时！");
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IP 错误！");
		}
	}
	//String localPath,String ip, String port, String userName, String userPwd
	 public  boolean connectServer(String host, String port, String user, String password, String defaultPath)
	      {
		 	localPath=defaultPath;
	        ftpClient = new FTPClient();
	        ftpClient.setDataTimeout(5000);
	        ftpClient.setConnectTimeout(60);
	        //ftpClient.setControlEncoding("UTF-8");
	        try {
				ftpClient.connect(host.trim(), Integer.parseInt(port));
			} catch (NumberFormatException e) {
				  System.out.println("==21=====");
				e.printStackTrace();
			} catch (SocketException e) {
				  System.out.println("==22=====");
				e.printStackTrace();
			} catch (IOException e) {
				//  System.out.println("FTP服务器："+host+":"+port+" 连接超时！");
				//e.printStackTrace();
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
	public  void closeServer() {
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
	public  void deleteFile(String fileName) {
		try {
			ftpClient.deleteFile(new String(fileName.getBytes("GBK"), "iso-8859-1"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传输文件到集中的服务上后并删除原FTP服务器中的文件
	 * @param str
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public  List<String> transferAndDelFiles(){
		FTPFile[] fs;
		List<String> nameList=null;
		try {
			ftpClient.changeWorkingDirectory(".");
		} catch (IOException e) {
			System.out.println("ftpClient切换账号出错");
			//e.printStackTrace();
		}
		
		try {
			ftpClient.enterLocalPassiveMode();
			fs = ftpClient.listFiles();
			if(fs.length!=0){
				nameList = new ArrayList<String>();
				for (FTPFile ff : fs) {
					String fileName=new String(ff.getName().getBytes("iso-8859-1"), "GBK");
					if (!ff.isDirectory()) {				
						String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
						System.out.println("fileType: "+fileType);
						if(fileType.equals("txt")){
							System.out.println("-----传输的文件名称为：" + fileName + "-------------------");
							File localFile = new File(localPath+"/"+ fileName);
							OutputStream is = new FileOutputStream(localFile);
							ftpClient.retrieveFile(ff.getName(), is);
							nameList.add(localPath+"/"+ fileName);
							is.close();
							deleteFile(fileName);
						}else{
							System.out.println("-----传输的文件名称为：" + fileName + "-------------------");
							File localFile = new File(localPath+"/"+ fileName);
							OutputStream is = new FileOutputStream(localFile);
							ftpClient.retrieveFile(ff.getName(), is);
							is.close();
							deleteFile(fileName);
						}
						
					} else {
						System.out.println(
								"------文件夹目录：" + fileName + "----------------------");
					}
				}
			}else{
				System.out.println("无新的检测数据生成！");
			}
			
		} catch (IOException e) {
			System.out.println("ftpClient文件传输出错！");
			//e.printStackTrace();
		}
		

		return nameList;
	}

	public  List<String> download(String ftpFile, FTPClient ftp) {
		List<String> list = new ArrayList<String>();
		String str = "";
		InputStream is = null;
		BufferedReader br = null;
		try {
			// 获取ftp上的文件
			is = ftp.retrieveFileStream(ftpFile);
			// 转为字节流
			br = new BufferedReader(new InputStreamReader(is));
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	 /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public  boolean deleteLocalFile(String fileName) {
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

	/**
	 * @param args
	 * @throws ParseException
	 */
	public  void main(String[] args) throws ParseException {
		FtpUtil ftp = new FtpUtil();
		try {
			ftp.transferAndDelFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


     
      
      

}
