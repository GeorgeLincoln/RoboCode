package cslib;
import robocode.*;
import java.awt.Color;

public class Lincoln extends AdvancedRobot
{
        Inimigo inimigo;	// nosso atual inimigo
        double PI = Math.PI;	//pi para funções matemáticas e de localização		
        int direcao = 1;				    
        double ForcaDoTiro;    //constante para a forçado tiro que vamos usar

        public void run() {
                inimigo = new Inimigo();
                inimigo.Distancia = 100000;                       // inicializa a distância para que possamos selecionar um inimigo
                setColors(Color.red,Color.blue,Color.black);	 // Para as cores do robô

                //ajustes da arma e do radar
                setAdjustGunForRobotTurn(true);
                setAdjustRadarForGunTurn(true);

                turnRadarRightRadians(2*PI);                    // vira o radar para ter uma visão do campo
                while(true) {
                        Movimentacao();				//movimentação do Robo
                        ForcaDoTiro();				// Seleciona a pôtencia do tiro
                        Scanner();				//Scannea o inimigo
                        fire(ForcaDoTiro);		        // atira segundo a ForcaDoTiro
                        execute();				//executa os comandos
                }
	}

        void ForcaDoTiro() {
			ForcaDoTiro= (inimigo.Distancia < 15? 5: 300/inimigo.Distancia);	
			     //seleciona a foça da bala com base em na distância do inimigo
        }

        void Movimentacao() {
                if (getTime()%20 == 0)  {       //a cada vinte 'ticks'
                        direcao *= -1;		//vira 180 graus
                        setAhead(direcao*1200);	//e anda para frente 
                }
                setTurnRightRadians(inimigo.bearing + (PI/2)); //Para desviar e atirar circulando o inimigo
				direcao = setAhead(direcao*1200);	//e anda para frente 
                
        }

        void Scanner() {
                double procura;
                if (getTime() - inimigo.ctime > 2) { 	//Se não tem ninguém por perto por um tempo maior que quatro ciclos
                       //procura = 360;		//Da um giro de 360 procurando
                       ahead(1200);//anda para frente

                } else {//se tiver inimigo

                        //Normaliza um ângulo para um ângulo relativo
                        // procurando onde o inimigo está
                        procura = setTurnRadarRight(Utils.normalRelativeAngleDegrees(getHeading() - getRadarHeading() + inimigo.getBearing()));
                        ahead(1200);//anda para frente
                }
                ahead(2000);//anda para frente
                //vira o radar até o angulo da variável
                setTurnRadarLeftRadians(Mira(procura));
        }

        //ajuste do angulo para atirar melhor 
        public void Mira(double angulo) {
			double A=getHeading()+angulo-getGunHeading();//local do inimigo mais angulo passado menos a direção da arma
			if (!(A > -180 && A <= 180)) {//se não tiver na frente(entre os 180 graus)
                                //procurando o inimigo rodando o radar
                                while (A <= -180) {
                                        A += 360; //da um giro de 360 graus para direita
				}
				while (A > 180) {//da um giro de 360 graus para direita
					A -= 360;
				}
			}
                        turnGunRight(A);
                        ahead(1200);//anda para frente
		}


        //Quando avistar o inimigo
        public void onScannedRobot(ScannedRobotEvent e) {
                //Se encontrar um inimigo
                if ((e.getDistance() < inimigo.Distancia)||(inimigo.nome == e.getName())) {
                        //encontar onde o inimigo está
                        double PosicaoDoInimigo = (getHeadingRadians()+e.getBearingRadians())%(2*PI);
                        //informações sobre o nosso inimigo
                        inimigo.nome = e.getName();
                        inimigo.x = getX()+Math.sin(PosicaoDoInimigo)*e.getDistance(); //coordenada do inimigo no eixo x
                        inimigo.y = getY()+Math.cos(PosicaoDoInimigo)*e.getDistance(); //coordenada do inimigo no eixo y
                        inimigo.bearing = e.getBearingRadians();
                        inimigo.ctime = getTime();				//tempo de jogo em que esta varredura foi produzida
                        inimigo.speed = e.getVelocity();
                        inimigo.Distancia = e.getDistance();
						ahead(500);
		}
				
	}
        //se for atingido por uma bala
        public void onHitByBullet(HitByBulletEvent e) {
                ahead(1200);
        }

        public void onRobotDeath(RobotDeathEvent e) {
                if (e.getName() == inimigo.nome)
                inimigo.Distancia = 20000; //para procurar um novo inimigo
        }
        
         //se for atingido
        public void onHitRobot(HitRobotEvent INI) {
                turnRight(INI.getBearing()); 
                fire(6);
        }
        //se colidir com a parede
        public void onHitWall(HitWallEvent e) {
                turnLeft(180); // move para esquerda
                ahead(12);//anda para frente
        }

}

class Inimigo {

        String nome;
        public double bearing; //distância entre o inimigo e eu;
        public long ctime; // para o tempo da varedura;
        public double speed; // velocidade do inimigo;
        public double Distancia; // distancia do einmigo

} 
 
