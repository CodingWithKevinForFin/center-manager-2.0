cd `dirname $0`;APPDIR=`pwd`;cd -

cd $APPDIR/..

for i in `find lib -type f -name '*.jar'`;do
  CP=$CP:$i;
done

java  -classpath "$CP" com.f1.encoder.AuthLoaderBuilder $1 $2 $3

