/*
   S N A K E      G A M E
*/

#include<dos.h>
#include<string.h>
#include<conio.h>
#include<stdlib.h>
#include<stdio.h>

typedef struct snake
{
	int x,y;
}node;

node arr[2800],a;        			// arr[]   : the co-od of snake's body
int maze[71][41];				// maze[]  : where maze is placed in the box
char far *vid_mem=(char *)0xB8000000;			// vid_mem : video memory
int key=72,lag=100,l=4,flag,ascii,scan; 	// key     : indicates the direction of snake
char *buffer,*g;				// lag     : time delay
						// l       : length
						// g       : for text scrolling
void chooselevel();				//buffer   : for storing screen attributes
void createmaze();
void savevideo(int,int,int,int);
void restorevideo(int,int,int,int);
void help_level();
void help_maze();
void help_game();
void quit();
void scroll(char t[],int xx,int yy)
{
	int k=strlen(t);
	int i;
	for(i=0;i<k;i++)
	{
		gotoxy(xx+i,yy);
		cprintf("%c",t[i]);
		sound(20);
		delay(50);
		nosound();
	}
}


void initialise()			// for making initial snake's body
{
	arr[0].x=40;
	arr[1].x=40;
	arr[2].x=40;
	arr[3].x=40;
	arr[4].x=40;

	arr[0].y=25;
	arr[1].y=26;
	arr[2].y=27;
	arr[3].y=28;
	arr[4].y=29;
}
void frame()				// drawing the border
{
	int xx,yy;
	textbackground(BLACK);
	textcolor(GREEN);
	gotoxy(29,2);cprintf("SNAKE - GAME by Dipanjan");
	gotoxy(18,50);cprintf("ESC to exit     PAUSE to pause    F1 for help");
	gotoxy(27,48);cprintf("Navigate using  ARROW keys");
	gotoxy(65,3);cprintf("SCORE : %3d",(l-4)*10);
	textcolor(YELLOW);
	for(xx=5,yy=4;xx<=75;xx++)
		{gotoxy(xx,yy);cprintf("Í");}
	for(xx=76,yy=5;yy<=45;yy++)
		{gotoxy(xx,yy);cprintf("º");}
	for(xx=5,yy=46;xx<=75;xx++)
		{gotoxy(xx,yy);cprintf("Í");}
	for(xx=4,yy=5;yy<=45;yy++)
		{gotoxy(xx,yy);cprintf("º");}
	textcolor(RED);
	gotoxy(4,4);cprintf("É");
	gotoxy(76,4);cprintf("»");
	gotoxy(4,46);cprintf("È");
	gotoxy(76,46);cprintf("¼");
}
void display()			//  for drawing the snake
{
	int i,xx,yy;
	static int xxx,yyy;
	frame();
	gotoxy(xxx,yyy);
	textbackground(BLACK);
	printf(" \b");
	xxx=arr[l].x;yyy=arr[l].y;
	for(i=0;arr[i].x!=0;i++)
		{
		xx=arr[i].x;
		yy=arr[i].y;
		gotoxy(xx,yy);
		if(i==0)
			{textcolor(GREEN);cprintf("*\b");}
		else
			{textcolor(RED);cprintf("O\b");}
		}
	textbackground(RED);textcolor(GREEN);
	gotoxy(1,1);
	switch(key)
		{
		case 72:{
			cprintf("UP");
			textbackground(BLACK);
			cprintf("   ");
			break;
			}
		case 75:{
			textbackground(RED);
			cprintf("LEFT");
			textbackground(BLACK);
			cprintf(" ");
			break;
			}
		case 77:{
			cprintf("RIGHT");
			break;
			}
		case 80:{
			textbackground(RED);
			cprintf("DOWN");
			textbackground(BLACK);
			cprintf(" ");
			break;
			}
		}
	textcolor(WHITE);textbackground(BLACK);
}
void  getkey()
{
	union REGS i,o;               //  72  UP   ARROW
	while(!kbhit());	      //  80 DOWN  ARROW
	i.h.ah=0;                     //  75 LEFT  ARROW
	int86(22,&i,&o);              //  77 RIGHT ARROW
	ascii=o.h.al;
	scan=o.h.ah;
}
void generate()				// for generating the  food
{
	int i=0,j,flag1;
	do
	{
	flag1=0;
	a.x=5+random(71);
	a.y=5+random(41);
	for(i=0;arr[i].x!=0;i++)
		{
		if((arr[i].x==a.x)&&(arr[i].y==a.y))
			flag1=1;
		}
	for(i=0;i<=70;i++)
		{
		for(j=0;j<=45;j++)
			{
			if((maze[i+5][j+5]==1)&&(i==a.x)&&(j==a.y))
				flag1=1;
			}
		}
	}while(flag1==1);
}
void moveup()
{
		int xx,yy,i,j;
		xx=arr[0].x;yy=arr[0].y-1;
		if(yy<5)
			yy=45;
		for(i=0;arr[i].x!=0;i++)
			{
			if((arr[i].x==xx)&&(arr[i].y==yy))
				{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
				}
			}
		for(i=0;i<=70;i++)
			{
			for(j=0;j<=40;j++)
				{
				if((maze[i][j]==1)&&(i+5==xx)&&(j+5==yy))
					{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
					}
				}
			}
		if(((arr[0].y-1==a.y)&&(arr[0].x==a.x))||((arr[0].x==a.x)&&(arr[0].y==5)&&(a.y==45)))
		{
			l++;
			for(i=l;i>=1;i--)
			{
			arr[i]=arr[i-1];
			}
			arr[0]=a;
			flag=0;
			sound(2400);delay(10);nosound();
		}
		else
		{
			for(i=l;i>0;i--)
				{
				arr[i]=arr[i-1];
				}
				arr[0].y--;
				if(arr[0].y<5)
					arr[0].y=45;
		}
}
void moveleft()
{
		int xx,yy,i,j;
		xx=arr[0].x-1;yy=arr[0].y;
		if(xx<5)
			xx=75;
		for(i=0;arr[i].x!=0;i++)
			{
			if((arr[i].x==xx)&&(arr[i].y==yy))
				{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
				}
			}
		for(i=0;i<=70;i++)
			{
			for(j=0;j<=40;j++)
				{
				if((maze[i][j]==1)&&(i+5==xx)&&(j+5==yy))
					{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
					}
				}
			}

		if(((arr[0].x-1==a.x)&&(arr[0].y==a.y))||((arr[0].y==a.y)&&(arr[0].x==5)&&(a.x==75)))
		{
			l++;
			for(i=l;i>=1;i--)
			{
			arr[i]=arr[i-1];
			}
			arr[0]=a;
			flag=0;
			sound(2400);delay(10);nosound();
		}
		else
		{
		for(i=l;i>0;i--)
			{
			arr[i]=arr[i-1];
			}
		arr[0].x--;
			if(arr[0].x<5)
				arr[0].x=75;
		}

}
void moveright()
{
		int xx,yy,i,j;
		xx=arr[0].x+1;yy=arr[0].y;
		if(xx>75)
			xx=5;
		for(i=0;arr[i].x!=0;i++)
			{
			if((arr[i].x==xx)&&(arr[i].y==yy))
				{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
				}
			}
		for(i=0;i<=70;i++)
			{
			for(j=0;j<=40;j++)
				{
				if((maze[i][j]==1)&&(i+5==xx)&&(j+5==yy))
					{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
					}
				}
			}

		if(((arr[0].x+1==a.x)&&(arr[0].y==a.y))||((arr[0].y==a.y)&&(arr[0].x==75)&&(a.x==5)))
		{
			l++;
			for(i=l;i>=1;i--)
			{
			arr[i]=arr[i-1];
			}
			arr[0]=a;
			flag=0;
			sound(2400);delay(10);nosound();
		}
		else
		{
		for(i=l;i>0;i--)
			{
			arr[i]=arr[i-1];
			}
		arr[0].x++;
			if(arr[0].x>75)
				arr[0].x=5;
		}

}
void movedown()
{
		int xx,yy,i,j;
		xx=arr[0].x;yy=arr[0].y+1;
		if(yy>45)
			yy=5;
		for(i=0;arr[i].x!=0;i++)
			{
			if((arr[i].x==xx)&&(arr[i].y==yy))
				{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
				}
			}
		for(i=0;i<=70;i++)
			{
			for(j=0;j<=40;j++)
				{
				if((maze[i][j]==1)&&(i+5==xx)&&(j+5==yy))
					{
				sound(1050);
				delay(2000);
				nosound();
				quit();
				exit(0);
					}
				}
			}

		if(((arr[0].y+1==a.y)&&(arr[0].x==a.x))||((arr[0].x==a.x)&&(arr[0].y==45)&&(a.y==5)))
		{
			l++;
			for(i=l;i>=1;i--)
			{
			arr[i]=arr[i-1];
			}
			arr[0]=a;flag=0;
			sound(2400);delay(10);nosound();
		}
		else
		{
		for(i=l;i>0;i--)
			{
			arr[i]=arr[i-1];
			}
		arr[0].y++;
			if(arr[0].y>45)
				arr[0].y=5;
		}

}


void move()
{
	if(ascii==49)
		{
		ascii=-2;
		fflush(stdin);
		}
	else
		{
		if(ascii==50)
			{
			delay(750);
			ascii=-2;
			fflush(stdin);
			}
		else
			{
			delay(lag);
			fflush(stdin);
			}
		}

	if(key==72)
		{
		moveup();                             	//  moving   UP;
		}

	if(key==75)				//  moving   LEFT
		{
		moveleft();
		}

	if(key==77)				//  moving   RIGHT
		{
		moveright();
		}

	if(key==80)				//  moving   DOWN
		{
		movedown();

		}
}

void main()

{
	int i;
	textmode(64);
	randomize();
	initialise();
	textbackground(BLACK);
	clrscr();
	chooselevel();
	clrscr();
	display();
	createmaze();
	while(1)
	{
	if(kbhit())
	getkey();
	if(ascii==43)
		{
		lag-=10;
		if(lag<=0)
			lag=10;
		ascii=-2;
		fflush(stdin);
		continue;
		}
	if(ascii==45)
		{
		lag+=10;
		if(lag>3000)
			lag=3000;
		ascii=-2;
		fflush(stdin);
		continue;
		}

		switch(scan)
		{
			case 72:	         //   move UP
				{
				if(key==80)
					{
					fflush(stdin);
					break;
					}
				else
					key=72;
				break;
				}
			case 75:		//move   LEFT
				{
				if(key==77)
					{
					fflush(stdin);
					break;
					}
				else
					key=75;
				break;
				}
			case 77:		//move RIGHT
				{
				if(key==75)
					{
					fflush(stdin);
					break;
					}
				else
					key=77;
				break;
				}
			case 80:		//move DOWN
				{
				if(key==72)
					{
					fflush(stdin);
					break;
					}
				else
					key=80;
				break;
				}
			case 1:
				{
				quit();
				exit(0);
				}
			case 59:{
				buffer=malloc(1000);
				savevideo(20,20,30,60);
				help_game();
				restorevideo(20,20,30,60);
				scan=-1;
				break;
				}
			}
	display();
	if(flag==0)
		{
		generate();
		textcolor(GREEN+BLINK);
		gotoxy(a.x,a.y);cprintf("X\b");
		textcolor(WHITE);
		flag=1;
		}
	move();
	}

}
void chooselevel()
{
	int xx,yy,f[3],b[3],pos=0;
	f[0]=2;b[0]=4;
	f[1]=2;b[1]=0;
	f[2]=2;b[2]=0;
	clrscr();
	textcolor(GREEN);
	textbackground(BLACK);
	for(xx=34,yy=23;xx<=46;xx++)
		{
		gotoxy(xx,yy);cprintf("Ä");
		}
	for(xx=34,yy=27;xx<=46;xx++)
		{
		gotoxy(xx,yy);cprintf("Ä");
		}
	for(xx=34,yy=24;yy<=26;yy++)
		{
		gotoxy(xx,yy);cprintf("³");
		}
	for(xx=46,yy=24;yy<=26;yy++)
		{
		gotoxy(xx,yy);cprintf("³");
		}
	gotoxy(34,23);cprintf("Ú");
	gotoxy(46,23);cprintf("¿");
	gotoxy(34,27);cprintf("À");
	gotoxy(46,27);cprintf("Ù");
	while(scan!=28)
	{
	textbackground(b[0]);textcolor(f[0]);
	gotoxy(35,24);cprintf("I N F A N T");
	textbackground(b[1]);textcolor(f[1]);
	gotoxy(35,25);cprintf("C H I L D  ");
	textbackground(b[2]);textcolor(f[2]);
	gotoxy(35,26);cprintf("A D U L T  ");
	textcolor(WHITE);textbackground(BLACK);
	gotoxy(32,20);cprintf("SELECT YOUR LEVEL");
	gotoxy(21,46);cprintf("Use  UP  &  DOWN  arrow keys to select");
	gotoxy(17,48);cprintf("F1 for help     ESC to exit     PAUSE to pause");
	getkey();

	if((scan==72)&&(pos==0))
		{
		sound(3000);delay(500);nosound();continue;
		}
	if((scan==80)&&(pos==2))
		{
		sound(3000);delay(500);nosound();continue;
		}
	if(scan==1)
		exit(0);
	if((scan!=72)&&(scan!=80)&&(scan!=1)&&(scan!=28)&&(scan!=59))
		{
		sound(3000);delay(500);nosound();continue;
		}
	switch(scan)
		{
		case 72:{
			b[pos]=0;
			b[--pos]=4;
			break;
			}
		case 80:{
			b[pos]=0;
			b[++pos]=4;
			break;
			}
		case 59:{
			buffer=malloc(500);
			savevideo(22,21,28,58);
			help_level();
			restorevideo(22,21,28,58);
			break;
			}
		}
	}
	switch(pos)
		{
		case 0:{
			lag=550;
			break;
			}
		case 1:{
			lag=250;
			break;
			}
		case 2:{
			lag=175;
			break;
			}
		}


	textcolor(15);
	textbackground(BLACK);
}
void createmaze()
{
	int xx=5,yy=5,yyy,i=0;
	char msg[]="CREATE MAZE";
	textbackground(RED);
	textcolor(GREEN+BLINK);
	for(yyy=15;yyy<=35;yyy++)
		{
		gotoxy(1,yyy);
		cprintf(" %c ",msg[i++]) ;
		gotoxy(1,++yyy);
		printf("   ");
		}
	textbackground(BLACK);
	gotoxy(5,5);
	scan=-2;
	while(scan!=28)
	{
	getkey();
	if(((xx==40)&&(yy==30)&&(scan==72))||((xx==40)&&(yy==24)&&(scan==80))||
		((xx==39)&&((yy>=25)&&(yy<=29))&&(scan==77))||
		((xx==41)&&((yy>=25)&&(yy<=29))&&(scan==75)))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	if((scan==72)&&(yy==5))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	if((scan==75)&&(xx==5))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	if((scan==77)&&(xx==75))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	if((scan==80)&&(yy==45))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	if(scan==1)
		exit(0);
	if((scan!=72)&&(scan!=75)&&(scan!=77)&&(scan!=80)&&(scan!=1)&&(scan!=28)&&(scan!=57)&&(scan!=59))
		{
		sound(1000);
		delay(1000);
		nosound();
		continue;
		}
	textcolor(WHITE+BLINK);
	switch(scan)
		{
		case 72:{
			if(maze[xx-5][yy-5]==0)
				{
				printf(" \b");
				}
			gotoxy(xx,--yy);
			if(maze[xx-5][yy-5]!=1)
				{
				cprintf("_");
				printf("\b");
				}
			break;
			}
		case 75:{
			if(maze[xx-5][yy-5]==0)
				{
				printf(" \b");
				}
			gotoxy(--xx,yy);
			if(maze[xx-5][yy-5]!=1)
				{
				cprintf("_");
				printf("\b");
				}
			break;
			}
		case 77:{
			if(maze[xx-5][yy-5]==0)
				{
				printf(" \b");
				}
			gotoxy(++xx,yy);
			if(maze[xx-5][yy-5]!=1)
				{
				cprintf("_");
				printf("\b");
				}
			break;
			}
		case 80:{
			if(maze[xx-5][yy-5]==0)
				{
				printf(" ");
				}
			gotoxy(xx,++yy);
			if(maze[xx-5][yy-5]!=1)
				{
				cprintf("_");
				printf("\b");
				}
			break;
			}
		case 57:{
			if(maze[xx-5][yy-5]==1)
				{
				maze[xx-5][yy-5]=0;
				printf(" \b");
				}
			else
				{
				maze[xx-5][yy-5]=1;
				textcolor(WHITE);
				cprintf("±");printf("\b");
				}


			break;
			}
		case 59:{
			buffer=malloc(1000);
			savevideo(21,18,29,71);
			help_maze();
			restorevideo(21,18,29,71);
			textbackground(BLACK);
			gotoxy(xx,yy);
			break;
			}
		case 28:{
			if(maze[xx-5][yy-5]==0)
			{
			gotoxy(xx,yy);
			printf(" ");
			}
			for(yyy=15;yyy<=35;yyy++)
			{
				gotoxy(1,yyy);
				textbackground(BLACK);
				cprintf("   ");
			}
			}

		}
	}
}

//* saves screen contents into allocated memory in RAM*/
void savevideo(int sr,int sc,int er,int ec)
{
	char far *v ;
	int i, j ;
	char *t;
	t=buffer;

	for ( i = sr ; i <= er ; i++ )
	{
		for ( j = sc ; j <= ec ; j++ )
		{
			v = vid_mem + i * 160 + j * 2 ;  /* calculate address */
			*t = *v ;  /* store character */
			v++ ;
			t++ ;
			*t = *v ;  /* store attribute */
			t++ ;
		}
	}
}

// restores screen contents from allocated memory in RAM */
void restorevideo(int sr,int sc,int er,int ec)
{
	char far *v ,*t=buffer;
	int i, j ;

	for ( i = sr ; i <= er ; i++ )
	{
		for ( j = sc ; j <= ec ; j++ )
		{
			v = vid_mem + i * 160 + j * 2 ;  /* calculate address */
			*v = *t ;  /* restore character */
			v++ ;
			t++ ;
			*v = *t ;  /* restore attribute */
			t++ ;
		}
	}
	free(buffer);
}
void help_level()
{
	int xx=23,yy=24,ii,jj;
	textbackground(YELLOW);
	textcolor(BLUE);
	for(jj=1;jj<=4;jj++)
	{
		gotoxy(xx,yy++);
		for(ii=12;ii<=46;ii++)
		cprintf(" ");
	}

	for(xx=23,yy=23;xx<=58;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=23,yy=28;xx<=58;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=22,yy=23;yy<=28;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	for(xx=58,yy=23;yy<=28;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	gotoxy(22,23);cprintf("É");
	gotoxy(58,23);cprintf("»");
	gotoxy(22,28);cprintf("È");
	gotoxy(58,28);cprintf("¼");

	g="Press UP & DOWN arrow key to select";scroll(g,23,24);
	g="ENTER key to proceed";scroll(g,23,25);
	g="ESC key to quit";scroll(g,23,26);
	g="Press any key to continue";scroll(g,23,27);
	getch();

}
void help_maze()
{
	int xx=20,yy=23,ii,jj;
	textbackground(YELLOW);
	textcolor(BLUE);
	for(jj=1;jj<=6;jj++)
	{
		gotoxy(xx,yy++);
		for(ii=12;ii<=62;ii++)
		cprintf(" ");
	}

	for(xx=20,yy=22;xx<=71;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=19,yy=29;xx<=71;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=19,yy=22;yy<=29;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	for(xx=71,yy=22;yy<=29;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	gotoxy(19,22);cprintf("É");
	gotoxy(71,22);cprintf("»");
	gotoxy(19,29);cprintf("È");
	gotoxy(71,29);cprintf("¼");

	g="Use ARROW keys to go to desired position";scroll(g,20,23);
	g="Hit SPACE bar to select that positon for maze";scroll(g,20,24);
	g="Hit SPACE bar again to delete maze in that position";scroll(g,20,25);
	g="RETURN key to start game";scroll(g,20,26);
	g="Press ESC to quit";scroll(g,20,27);
	g="Press any key to continue";scroll(g,20,28);
	getch();
}

void help_game()
{
	int xx=23,yy=23,ii,jj;
	textbackground(YELLOW);
	textcolor(BLUE);
	for(jj=1;jj<=6;jj++)
	{
		gotoxy(xx,yy++);
		for(ii=12;ii<=46;ii++)
		cprintf(" ");
	}

	for(xx=23,yy=22;xx<=57;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=23,yy=29;xx<=57;xx++)
		{
		gotoxy(xx,yy);
		cprintf("Í");
		}
	for(xx=22,yy=22;yy<=29;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	for(xx=57,yy=22;yy<=29;yy++)
		{
		gotoxy(xx,yy);
		cprintf("º");
		}
	gotoxy(22,22);cprintf("É");
	gotoxy(57,22);cprintf("»");
	gotoxy(22,29);cprintf("È");
	gotoxy(57,29);cprintf("¼");

	g="Use ARROW keys to direct the snake";scroll(g,23,23);
	g="Hit '+' to increase your speed";scroll(g,23,24);
	g="Hit '-' key to decrease your speed";scroll(g,23,25);
	g="Press 1 to move fast";scroll(g,23,26);
	g="Press 2 to slow down";scroll(g,23,27);
	g="Press any key to continue";scroll(g,23,28);
	getch();
}
void quit()
{
	g="YOUR SCORE : ";
	textcolor(GREEN);textbackground(RED);
	gotoxy(30,24);cprintf("ÉÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍ»");
	gotoxy(30,25);cprintf("º                º");
	gotoxy(30,26);cprintf("ÈÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍÍ¼");
	textbackground(BLACK);textcolor(WHITE);
	scroll(g,31,25);
	cprintf("%3d",(l-4)*10);
	getch();
}







