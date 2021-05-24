import urllib.request  # 导入用于打开URL的扩展库模块
import urllib.parse
import re  # 导入正则表达式模块
import pymysql.cursors

webUrl = "https://imeizi.me"
webUrlHot = webUrl + "/sort/hot"

# 连接数据库
connect = pymysql.Connect(
    host='106.15.94.235',
    port=3306,
    user='root',
    passwd='as153759',
    db='robot',
    charset='utf8'
)


def open_url(url):
    req = urllib.request.Request(url)  # 将Request类实例化并传入url为初始值，然后赋值给req
    # 添加header，伪装成浏览器
    req.add_header('User-Agent',
                   'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 '
                   'Safari/537.36 SE 2.X MetaSr 1.0')
    # 访问url，并将页面的二进制数据赋值给page
    page = urllib.request.urlopen(req)
    # 将page中的内容转换为utf-8编码
    html = page.read().decode('utf-8')
    return html


# 获取hot页article
def opsForHotArticle(html):
    pArticle = r'<a href="(/article/\d+/)"'
    pImage = r'data-src="/static/images/[^"]+\.jpg'
    articleUrlArr = re.findall(pArticle, html)
    for articleUrl in articleUrlArr:
        imgUrl = webUrl + articleUrl
        imgHtml = open_url(imgUrl)
        imgUrlArr = re.findall(pImage, imgHtml)
        for tempImgUrl in imgUrlArr:
            imgUrl = webUrl + tempImgUrl[10:]
            print(imgUrl)
            if opsForMysqlQueryForExist(imgUrl):
                opsForMysqlHandle(imgUrl)


def opsForMysqlHandle(inputUrl):
    cursor = connect.cursor()
    sql = "INSERT INTO sys_color_img (url) VALUES ( '%s')"
    data = inputUrl
    cursor.execute(sql % data)
    connect.commit()
    print('成功插入' + inputUrl + ",一共有" + str(opsForMysqlQueryAll()) + "条")


def opsForMysqlQueryForExist(queryUrl):
    cursor = connect.cursor()
    sql = "SELECT * FROM sys_color_img WHERE url = '%s' "
    data = queryUrl
    cursor.execute(sql % data)
    return cursor.rowcount <= 0


def opsForMysqlQueryAll():
    cursor = connect.cursor()
    sql = "SELECT * FROM sys_color_img "
    cursor.execute(sql)
    return cursor.rowcount


if __name__ == '__main__':

    currPage = 1

    nextPageElement = "next-page"
    url = webUrlHot + "/?page=" + str(currPage)

    hotHtml = open_url(url)
    opsForHotArticle(hotHtml)
    haveNextPage = nextPageElement in hotHtml

    while haveNextPage:
        currPage += 1
        url = webUrlHot + "/?page=" + str(currPage)
        hotHtml = open_url(url)
        opsForHotArticle(hotHtml)
