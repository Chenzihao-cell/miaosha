package czihao.miaosha.domain;

import java.util.Date;

public class MiaoshaUser {
	private Long id;			//用户ID，这里用“手机号码”作为用户ID
	private String nickname;	//用户昵称
	private String password;	//MD5(MD5(pass明文+固定salt) + salt)
	private String salt;		//加点盐，噪声
	private String head;		//用户头像，云存储的ID
	private Date registerDate;	//用户注册时间
	private Date lastLoginDate;	//最后一次登录时间
	private Integer loginCount;	//登录次数

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public Integer getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}
}
