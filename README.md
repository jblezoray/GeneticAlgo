# About 

This repository is just an experimentation around genetic algorithms. 

Most credits on this code goes to github user Murgio, whose code I refactored and optimized.
See his [Generic Algorithm Montage](https://github.com/Murgio/Genetic-Algorithm-Montage) repository. 

## What does it do ? 

Package `fr.jblezoray.mygeneticalgo` contains an implementation of a generic algorithm.  Then, there are sample applications of the genetic algorithm in packages `fr.jblezoray.mygeneticalgo.samples.helloworld`, `fr.jblezoray.mygeneticalgo.samples.facemashup`, and `fr.jblezoray.mygeneticalgo.sample.disksimage`.

### sample #1: Hello world 

Generates an "hello world" :
```Generation     50 (0,00170)--> Ldkpd!xeqlg . Lc!Lpqak.Lnrol boy!ndmpgdpeno.gu.gpwq scqzb.issimzw!bjjt ok iqlsqtfsklr.iv!wc.kgua hf pflf.fylqz.fgljbzjiqs.
Generation    100 (0,00633)--> Ldkmo!xoqlc ! Lc!Lpqek.Lnrul ept!ugmpkdoeno.gu!gaww sewte fmnimzé!damt la cqlpntisklm.er lc ngua hl pbde fydnr fmoressipn.
Generation    150 (0,01639)--> Hdkmo worlc ! Le!Lpsem!Iorum ett!simpmdment bu!gauy sewte flomnzé!damr la colporision.er lc misf hn pbfe avanr imoressipn.
Generation    200 (0,02778)--> Heklo world ! Le!Losem!Ipsum est silpmement cu eauy tewte emokozé!danr la composition.eu la!misc en pafe avant imoressinn.
Generation    250 (0,12500)--> Hemko world ! Le!Lorem Ipsum est simplement cu eaux texte employé!daos la comppsition et la mise en page avant impression.
Generation    300 (0,25000)--> Helko world ! Le!Lorem Ipsum est simplement cu faux texte employé dans la composition es la mise en page avant impression.
Generation    350 (0,33333)--> Hello world ! Le Lorem Ipsum est simplement cu!faux texte!employé dans la composition et la mise en page avant impression.
Generation    400 (0,25000)--> Iello world ! Le Lorem Ipsum est simplement cu!faux texte employé dant la composition et la mise en page avant impression.
Generation    450 (0,25000)--> Hello world ! Le Lnrem Ipsum est simpldment cu faux texte employé dant la composition et la mise en page avant impression.
Generation    500 (0,20000)--> Hello world ! Le Lorem Ipsum est simpldmeot!cu fbux texte employé dans la composition et la mise en page avant impression.
Generation    550 (0,25000)--> Hello world ! Le Lorem Ipsum est sjmpldment cu fbux texte employé dans la composition et la mise en page avant impression.
Generation    600 (0,25000)--> Hello world ! Le Lorem Ipsum est simplfment cu fbux texte employé dans la composition eu la mise en page avant impression.
```


### sample #2: face mashup

Generates a face by combinating instances of itself.

input:

<img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/facemashup/match.png" width="130">

generation 1, 100, 500, 1000, and 2000: 

<img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/facemashup/generation_0000001.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/facemashup/generation_0000100.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/facemashup/generation_0000500.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samplesfacemashup//generation_0001000.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/facemashup/generation_0002000.png" width="130">


### sample #3: Generates an images composed of disks

This sample generates an Generates an approximation of an image by composing arbitrary disks. 

input: 
  
<img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/monalisa_croped.png" width="130">

output: 

generation 200, 1000, 2000, 5000, and 10000: 

<img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/gen-0000200-0%2C442849.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/gen-0001000-0%2C720204.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/gen-0002000-0%2C858271.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/gen-0005000-1%2C045960.png" width="130"><img src="https://raw.githubusercontent.com/jblezoray/GeneticAlgo/master/samples/disksimage/gen-0010000-1%2C192829.png" width="130">


## How to run ?  

Install a java jdk >=8 and maven >=3.0.5.

First, compile the project : 
```
$ mvn package
```

Run sample #1 (helloworld) : 
```
$ java -cp target/myGeneticAlgo-0.0.1-SNAPSHOT.jar fr.jblezoray.mygeneticalgo.sample.helloworld.Main
``` 

Run sample #2 (facemashup) : 
```
$ mkdir statusDir
$ java -cp target/myGeneticAlgo-0.0.1-SNAPSHOT.jar fr.jblezoray.mygeneticalgo.sample.facemashup.Main samples/facemashup/match.png samples/facemashup/mask.png statusDir
```
The `statusDir` contains intermediate results. 

Run sample #2 (disksimage) :
```
$ mkdir statusDir
$ java -cp target/myGeneticAlgo-0.0.1-SNAPSHOT.jar fr.jblezoray.mygeneticalgo.sample.disksimage.Main samples/disksimage/monalisa_croped.png statusDir
```


Have fun !