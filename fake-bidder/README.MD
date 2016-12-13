# Fake bidder utility

This application provides access to fake RTB bidders.

## Usage

Run application using **sbt "run -p \<port\>"** where port - application port.

Send POST requests in given format:
 
**http://\<host\>:\<port\>?timeout=\<timeout\>\&price=\<price\>\&    modifier=\<modifier\>\&winhost=\<winhost\>**

### Parameters:

- **\<host\>** - application host,
- **\<port\>** - application port,
- **\<timeout\>** - waiting time before response,
- **\<price\>** - bid price,
- **\<modifier\>** = bid response modifier, possible bid response options:
    * invalidjson - JSON with invalid syntax,
    * invaliddata - invalid data,
    * nobidnocontent - empty body and 204 status code,
    * nobidemptyjson - empty JSON object,
    * nobidemptyseatbid - empty seatbid array,
    * winnotice-withoutadm - both nurl and adm in response,
    * winnotice-withadm - only nurl without adm in response,
    * winnotice-broken - only nurl in response, returning 500 status code,
    * winnotice-timeout-**\<win-timeout\>** - only nurl in response, returning adm after **\<win-timeout\>**,
- **\<winhost\>** - host for win notice, required if winnotice-* modifier specified.
