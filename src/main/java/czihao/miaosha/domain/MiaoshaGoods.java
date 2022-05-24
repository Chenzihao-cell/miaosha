package czihao.miaosha.domain;

import java.util.Date;

public class MiaoshaGoods {
	private Long id;			//秒杀商品表的主键
	private Long goodsId;		//商品Id
	private Integer stockCount;	//库存量
	private Date startDate;		//秒杀开始时间
	private Date endDate;		//秒杀结束时间

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public Integer getStockCount() {
		return stockCount;
	}
	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
