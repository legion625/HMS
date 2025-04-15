# 使用官方 Tomcat 映像
FROM tomcat:9.0-jdk17

# 清除預設 webapps（避免干擾）
RUN rm -rf /usr/local/tomcat/webapps/*

# 複製Docker上用到的tomcat設定
COPY deployment/renderDocker/tomcat/conf/ /usr/local/tomcat/conf/

# 複製你的 .war 到 webapps 資料夾，Render 會在這裡自動啟動
COPY release/HMS_release.war /usr/local/tomcat/webapps/ROOT.war

# 如果你需要設定時區或其他 ENV，可加在這邊
# ENV TZ=Asia/Taipei

EXPOSE 8080
