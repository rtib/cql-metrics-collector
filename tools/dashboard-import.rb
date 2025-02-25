#!/usr/bin/env ruby

require 'rubygems'
require 'json'

def traverse(obj, &block)
  if obj.is_a?(Hash)
    obj.each do |k, v|
      if k == 'datasource'
        v = yield(k, v)
      end
      obj[k] = traverse(v, &block)
    end
  elsif obj.is_a?(Array)
    obj.map! { |v| traverse(v, &block) }
  end
  obj
end

## Main
if ARGV.length != 1
  puts "Usage: #{$0} <dashboard.json>"
  exit 1
end

# Read the JSON file
puts "Reading JSON file: #{ARGV[0]}"
json = JSON.parse(File.read(ARGV[0]))
title = json['title']
puts "Parsed dashboard title: #{title}"

# Remove the inputs
json['__inputs'] = []
json.delete('version')

cds = ds = 0

# Replace the variables
traverse(json) do |key, value|
  ds += 1
  if value['type'] == 'prometheus'
    value = nil
    cds += 1
  end
  value
end

puts "Found #{ds} datasources, changed #{cds}"

# Write the JSON file
File.write(File.join(__dir__, '..', 'dashboards', "#{title}.json"), JSON.pretty_generate(json))
