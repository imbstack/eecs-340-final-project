#!/usr/bin/env perl

# Simple interface to GNU Plot so that lazy people like me dont have to 
# know too much to generate quick plots.
#
# Version 2.5.1
#
# Mark Allman (mallman@icir.org)
# Last Updated: Fri Nov 19, 2010
#
# Acknowledgments:
#   Ethan Blanton added the "using" option.
#   Joseph Ishac for extensions to the "-F" option.
#   Ruoming Pang for the "-d" and "-ps" options.

$plottype = "linespoints";
$ticks = 1;
$grid = 1;
$rangebars = 0;
$outfile = "";
$comfile = 0;
$heading = "";
$logaxis = "";
$latex = 0;
$png = 0;
$threed = 0;
$keybox = 1;
$tmpfile = "/tmp/gpthis.$$";
$range = "";
$fontsize = "12";
$color = "";
$linewidth = "";
$dlength = 1;
$xtics = "";
$ytics = "";
$pointsize = "";
$default_ext = "ps";
$year = "";

if ($#ARGV == -1)
{
    &usage;
}
while ($_ = $ARGV [0], /^-/)
{
    shift;
    if (/^-x$/)
    {
	$xlabel = $ARGV [0];
	shift;
    }
    elsif (/^-y$/)
    {
	$ylabel = $ARGV [0];
	shift;
    }
    elsif (/^-z$/)
    {
	$zlabel = $ARGV [0];
	shift;
    }
    elsif (/^-t$/)
    {
	$plottype = $ARGV [0];
	shift;
    }
    elsif (/^-h$/)
    {
	&usage;
    }
    elsif (/^-H$/)
    {
	$heading = $ARGV [0];
	shift;
    }
    elsif (/^-f$/)
    {
	$formula = $ARGV [0];
	shift;
    }
    elsif (/^-ft$/)
    {
	$formula_title = $ARGV [0];
	shift;
    }
    elsif (/^-F$/)
    {
	if ($ARGV[0] =~ /\%/)
	{	
	    $format = $ARGV [0];
	}
	else
	{
	    $sformat[@sformat] = "$ARGV[0] \'$ARGV[1]\'";
	    shift;
	}
	shift;
    }
    elsif (/^-g$/)
    {
	$grid = 0;
    }
    elsif (/^-T$/)
    {
	$ticks = 0;
    }
    elsif (/^-o$/)
    {
	$outfile = $ARGV [0];
	shift;
    }
    elsif (/^-c$/)
    {
	$comfile = 1;
    }
    elsif (/^-l$/)
    {
	$logaxis = $ARGV [0];
	shift;
    }
    elsif (/^-lw$/)
    {
	$linewidth = "lw " . $ARGV [0];
	shift;
    }
    elsif (/^-ps$/)
    {
        $pointsize = "ps " . $ARGV [0];
        shift;
    }
    elsif (/^-k$/)
    {
	$key = $ARGV [0];
	shift;
    }
    elsif (/^-K$/)
    {
	$keybox = 0;
    }
    elsif (/^-L$/)
    {
	$latex = 1;
        $default_ext = "eps";
    }
    elsif (/^-P$/)
    {
	$png = 1;
        $default_ext = "png";
    }
    elsif (/^-r$/)
    {
	$range = $ARGV [0];
	shift;
    }
    elsif (/^-R$/)
    {
	$rangebars = 1;
    }
    elsif (/^-s$/)
    {
	$fontsize = $ARGV [0];
	shift;
    }
    elsif (/^-3$/)
    {
	$threed = 1;
    }
    elsif (/^-C$/)
    {
	$color = "color";
    }
    elsif (/^-xt$/)
    {
	$xtics = $ARGV [0];
	shift;
    }
    elsif (/^-yt$/)
    {
	$ytics = $ARGV [0];
	shift;
    }
    elsif (/^-d$/)
    {
        $dlength = $ARGV [0];
        shift;
    }
    elsif (/^-D$/)
    {
        $delim = $ARGV [0];
        shift;
    }
    elsif (/^-Y/)
    {
        $year = "set xdata time\nset timefmt \"%s\"\nset format x \"%Y\"\n";
    }
}
if ($key && !$keybox)
{
    print "Can't use -k and -K together\n";
    &usage;
}
if ($latex && $png)
{
    print "Can't use -L and -P together\n";
    &usage;
}

open (CMD,"> $tmpfile");
if ($latex)
{
    print CMD "set term post eps ", $color, "\"Times-Roman\" ", 
	$fontsize, " dashlength ", $dlength, "\n";
}
elsif ($png)
{
    if (length ($color))
    {
        print CMD "set term png large size 1024,768\n";
    }
    else
    {
        print CMD "set terminal png large size 1024,768 xffffff ";
        print CMD "x000000 x202020 x404040 x606060 x808080 xA0A0A0 ";
        print CMD "xC0C0C0 xE0E0E0\n";
    }
}
else
{
    print CMD "set term post landscape ", $color, " \"Times-Roman\" ", 
      $fontsize, " dashlength ", $dlength, "\n";
}
if (!$ticks)
{
    print CMD "set tics out\n";
}
if ($grid)
{
    print CMD "set grid\n";
}
if ($ylabel)
{
    print CMD "set ylabel '$ylabel' 0.5,0\n";
}
if ($xlabel)
{
    print CMD "set xlabel '$xlabel'\n";	
}
if ($zlabel)
{
    print CMD "set zlabel '$zlabel'\n";	
}
if ($key)
{
    print CMD "set key $key\n";
}
if (!$keybox)
{
    print CMD "unset key\n";
}
print CMD $year;
if ($format)
{
    print CMD "set format $format\n";
}
foreach $i (@sformat)
{
    print CMD "set format $i\n";
}
if ($heading)
{
    print CMD "set title \"$heading\"\n";
}
if (length ($xtics))
{
    print CMD "set xtics $xtics\n";
}
if (length ($ytics))
{
    print CMD "set ytics $ytics\n";
}
if ($logaxis)
{
    print CMD "set logscale ", $logaxis, "\n";
}
if ($delim)
{
    print CMD "set datafile separator \"", $delim, "\"\n";
}
if ($formula)
{
    if ($threed)
    {
	print CMD "splot $range $formula";
    }
    else
    {
	print CMD "plot $range $formula";
    }
    if ($formula_title)
    {
	print CMD " title \"$formula_title\"";
    }
    if ($w != "")
    {
	print CMD " with $plottype ";
	print CMD "lt $w " if ($plottype =~ /line/);
	print CMD "pt $w " if ($plottype =~ /point/);
	print CMD $linewidth;
        print CMD $pointsize;
    }
    else
    {
	print CMD " with $plottype $linewidth $pointsize";
    }
}
foreach $i (0 .. $#ARGV)
{
    $_ = $ARGV [$i];
    $title = "";
    $cols = "";
    $w = "";
    if (/\(/ || /\{/)
    {
	if (/\{.*\(.*\(/)
	{
 	    ($filename,$w,$title,$cols) = /^(.*)\{(.*)\}\((.*)\)\((.*)\)$/;
	}
 	elsif (/\(.*\(/)
 	{
 	    ($filename,$title,$cols) = /^(.*)\((.*)\)\((.*)\)$/;
 	}
	elsif (/\{.*\(/)
	{
	    ($filename,$w,$title) = /^(.*)\{(.*)\}\((.*)\)$/;
	}
 	elsif (/\(/)
 	{
 	    ($filename,$title) = /^(.*)\((.*)\)$/;
 	}
	else
	{
	    ($filename,$w) = /^(.*)\{(.*)\}$/;
	}
    }
    else
    {
	$filename = $ARGV [$i];
    }
    if ($i == 0)
    {
	if ($formula)
	{
	    print CMD ", ";
	}
	else
	{
	    if ($threed)
	    {
		print CMD "splot $range ";
	    }
	    else
	    {
		print CMD "plot $range ";
	    }
	}
    }
    else
    {
	print CMD ", ";
    }
    print CMD "\"$filename\" ";
    if ($cols)
    {
	print CMD "using $cols ";
    }
    if ($title)
    {
	print CMD "title \"$title\" ";
    }
    if ($w != "")
    {
	print CMD " with $plottype ";
	print CMD "lt $w " if ($plottype =~ /line/);
	print CMD "pt $w " if ($plottype =~ /point/);
	print CMD $linewidth;
    }
    else
    {
	print CMD " with $plottype $linewidth";
    }
    if ($rangebars)
    {
	print CMD ", \"$filename\" notitle with errorbars 1";
    }
}
print CMD "\n";
close (CMD);
if ($comfile)
{
    if (!$outfile)
    {
	$outfile = "graph.commands";
    }
    `cp $tmpfile $outfile`;
    `rm -f $tmpfile`;
}
else
{
    if (!$outfile)
    {
	$outfile = "graph." . $default_ext;
    }
    `gnuplot $tmpfile > $outfile`;
    `rm -f $tmpfile`;
}
exit (0);

sub usage
{
    print "gpthis [options] [filename1[{with1}](title1)[(using1)] \\\n";
    print "       filename2[{with2}](title2)[(using2)] ...]\n";
    print "\t-3\tGenerate a 3-d plot\n";
    print "\t-c\tGenerate a generic command file\n";
    print "\t-C\tGenerate a color plot\n";
    print "\t-d X\tset the dash length to X\n";
    print "\t-D X\tset data delimiter to X\n";
    print "\t-f F\tGraph formula F\n";
    print "\t-ft X\tSet formula key entry to X\n";
    print "\t-F X\tUse format \"X\" for labeling\n";
    print "\t-g\tDo not plot on grid \n";
    print "\t-h\tShow usage\n";
    print "\t-H X\tDefine graph heading as X\n";
    print "\t-k X\tSpecify location of key\n";
    print "\t-K\tDo not draw a key box\n";
    print "\t-l xyz\tUse logscale on the given axis\n";
    print "\t-L\tGenerate EPS graph (LaTeX friendly)\n";
    print "\t-lw X\tUse X as the line width\n";
    print "\t-ps X\tUse X as the point size\n";
    print "\t-P\tGenerate a PNG file, rather than postscript\n";
    print "\t\t(font size not setable when generating PNG files)\n";
    print "\t-o F\tOutput to file F\n";
    print "\t-r R\tPlot using the range R\n";
    print "\t-R\tPlot data with range bars\n";
    print "\t-s X\tSet font size to X\n";
    print "\t-t\tDefine type of plot (default - linespoints)\n";
    print "\t-T\tTurn ticks off\n";
    print "\t-x\tDefine label for X Axis\n";
    print "\t-xt X\tDefine x-axis tick marks\n";
    print "\t-y\tDefine label for Y Axis\n";
    print "\t-Y\tTreat x-axis data as time and print as year\n";
    print "\t-yt X\tDefine y-axis tick marks\n";
    exit (0);
}
