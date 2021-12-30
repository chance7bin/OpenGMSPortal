//生成假数据
for (let i = 0, len = 120; i < len; i++) {
    let dataItem = {
        tempID: i,
        date: new Date().toLocaleString(),
        name: i.toString() + "xxx",
        count: Math.floor(Math.random() * 100),
        status: Math.floor(Math.random()* 10)>5? "未知" : "正常",
        mmmid:"xxxxxxxxx"
    };
    this.totalData.push(dataItem);
    this.tableData=this.totalData;
}
this.totalNum=this.tableData.length