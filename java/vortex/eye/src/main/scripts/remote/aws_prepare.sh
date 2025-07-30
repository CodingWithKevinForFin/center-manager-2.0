#!/bin/bash

HOST=$1
DOMAIN=$2
DIR_DATA=$3
DIR_PKG=$4

if [ "x" != "x$DOMAIN" ]; then
	FQDN="$HOST.$DOMAIN"
else
	FQDN="$HOST"	
fi

echo "creating data [$DIR_DATA] / pkg [$DIR_PKG] dirs with links"

mkdir -p $DIR_DATA
ln -sf $DIR_DATA /
mkdir -p $DIR_PKG
ln -sf $DIR_PKG /

echo "changing hostname to $FQDN"

cp /etc/hosts /etc/hosts_backup_`date +%Y%m%d`
sed "s/localhost.*$/$FQDN $HOST/g" /etc/hosts_backup_`date +%Y%m%d` | sudo tee /etc/hosts > /dev/null
cp /etc/sysconfig/network /etc/sysconfig/network_backup_`date +%Y%m%d`
sed "s/^HOSTNAME.*$/HOSTNAME=$FQDN/g" /etc/sysconfig/network_backup_`date +%Y%m%d` | sudo tee /etc/sysconfig/network > /dev/null
hostname $FQDN

echo "creating script to update dns entries"
SCRIPT="/etc/dnsupdate.sh"
F=/tmp/nsupd.txt
cat << DONE > $SCRIPT
#!/bin/bash

exec 1>/tmp/dnsupdate.log 2>&1
HOST=\$1

echo "\`date\` running dnsupdate.sh with host \$HOST"

IP=\`wget -qO- http://169.254.169.254/latest/meta-data/public-ipv4\`
PTR=\`echo \$IP |  sed -r 's/^([0-9]+).([0-9]+).([0-9]+).([0-9]+).*$/\4.\3.\2.\1.in-addr.arpa/'\`
echo "update delete \$HOST 86400 a" > $F
echo "update add \$HOST 86400 a \$IP" >> $F
echo "send" >> $F
echo "update delete \$PTR 86400 ptr" >> $F
echo "update add \$PTR 86400 ptr \$HOST" >> $F
echo "send" >> $F

nsupdate $F
DONE

chmod u+x $SCRIPT


echo "setting up crontab to update the dns every hour"
(/usr/bin/crontab -l | sed  '/^.*dnsupdate.sh.*$/d' 2>/dev/null; echo "0 * * * * $SCRIPT $FQDN"; echo "@reboot $SCRIPT $FQDN";) | /usr/bin/crontab -u root -

echo "running $SCRIPT $FQDN the first time"
$SCRIPT $FQDN