#!/usr/bin/env perl
# Licensed to Elasticsearch under one or more contributor
# license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright
# ownership. Elasticsearch licenses this file to you under
# the Apache License, Version 2.0 (the "License"); you may
# not use this file except in compliance  with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on
# an 'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
# either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

use strict;
use warnings;

use HTTP::Tiny;
use IO::Socket::SSL 1.52;

my $Base_URL  = 'https://api.github.com/repos/';
my $User_Repo = 'elasticsearch/elasticsearch/';
my $Issue_URL = "http://github.com/${User_Repo}issues/issue/";

my @Groups       = qw(breaking feature enhancement bug regression doc test);
my %Group_Labels = (
    breaking    => 'Breaking changes',
    doc         => 'Docs',
    feature     => 'New features',
    enhancement => 'Enhancements',
    bug         => 'Bug fixes',
    regression  => 'Regression',
    test        => 'Tests',
    other       => 'Not classified',
);

use JSON();
use Encode qw(encode_utf8);

my $json = JSON->new->utf8(1);

my %All_Labels = fetch_labels();

my $version = shift @ARGV
    or dump_labels();

dump_labels("Unknown version '$version'")
    unless $All_Labels{$version};

my $format = shift @ARGV || "html";

my $issues = fetch_issues($version);
dump_issues( $version, $issues );

#===================================
sub dump_issues {
#===================================
    my $version = shift;
    my $issues  = shift;

    $version =~ s/v//;
    my ( $day, $month, $year ) = (gmtime)[ 3 .. 5 ];
    $month++;
    $year += 1900;

    for my $group ( @Groups, 'other' ) {
        my $group_issues = $issues->{$group} or next;
        $format eq 'html' and print "<h2>$Group_Labels{$group}</h2>\n\n<ul>\n";
        $format eq 'markdown' and print "## $Group_Labels{$group}\n\n";
        
        for my $header ( sort keys %$group_issues ) {
            my $header_issues = $group_issues->{$header};
            my $prefix        = "<li>";
            if ($format eq 'html') {
                if ( $header && @$header_issues > 1 ) {
                    print "<li>$header:<ul>";
                    $prefix = "<li>";
                }
                elsif ($header) {
                    $prefix = "<li>$header: ";
                }
            }
            for my $issue (@$header_issues) {
                my $title = $issue->{title};
                if ( $issue->{state} eq 'open' ) {
                    $title .= " [OPEN]";
                }
                my $number = $issue->{number};
                $format eq 'markdown' and print encode_utf8( "* "
                        . $title
                        . qq( [#$number](${Issue_URL}${number})\n)
                );
                $format eq 'html' and print encode_utf8( $prefix
                        . $title
                        . qq[ <a href="${Issue_URL}${number}">#${number}</a></li>\n]
                );
            }
            if ($format eq 'html' && $header && @$header_issues > 1 ) {
                print "</li></ul></li>\n";
            }
        }
        $format eq 'html' and print "</ul>";
        print "\n\n"
    }
}

#===================================
sub fetch_issues {
#===================================
    my $version = shift;
    my @issues;
    for my $state ( 'open', 'closed' ) {
        my $page = 1;
        while (1) {
            my $tranche
                = fetch( $User_Repo
                    . 'issues?labels='
                    . $version
                    . '&pagesize=100&state='
                    . $state
                    . '&page='
                    . $page )
                or die "Couldn't fetch issues for version '$version'";
            last unless @$tranche;
            push @issues, @$tranche;
            $page++;
        }
    }

    my %group;
ISSUE:
    for my $issue (@issues) {
        my %labels = map { $_->{name} => 1 } @{ $issue->{labels} };
        my $header = $issue->{title} =~ s/^([^:]+):\s+// ? $1 : '';
        for (@Groups) {
            if ( $labels{$_} ) {
                push @{ $group{$_}{$header} }, $issue;
                next ISSUE;
            }
        }
        push @{ $group{other}{$header} }, $issue;
    }

    return \%group;
}

#===================================
sub fetch_labels {
#===================================
    my $labels = fetch( $User_Repo . 'labels' )
        or die "Couldn't retrieve version labels";
    return map { $_ => 1 } grep {/^v/} map { $_->{name} } @$labels;
}

#===================================
sub fetch {
#===================================
    my $url      = $Base_URL . shift();
    my $response = HTTP::Tiny->new->get($url);
    die "$response->{status} $response->{reason}\n"
        unless $response->{success};

    #    print $response->{content};
    return $json->decode( $response->{content} );
}

#===================================
sub dump_labels {
#===================================
    my $error = shift || '';
    if ($error) {
        $error = "\nERROR: $error\n";
    }
    my $labels = join( "\n     - ", '', ( sort keys %All_Labels ) );
    die <<USAGE
    $error
    USAGE: $0 version > outfile

    Known versions:$labels

USAGE

}
