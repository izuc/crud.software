<?php
namespace App\Http\Controllers\Api\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Validator;
use Illuminate\Support\Facades\Storage;

class FileUploadController extends Controller
{
    public function upload(Request $request)
    {
 
       $validator = Validator::make($request->all(),[ 
              'file' => 'required|mimes:doc,docx,pdf,txt,csv|max:2048',
        ]);   
 
        if($validator->fails()) {          
            
            return response()->json(['error'=>$validator->errors()], 401);                        
         }  
 
  
         if ($file = $request->file('file')) {

            $name = $file->getClientOriginalName();
            $uid=rand(10000 , 99999);
            $filename = pathinfo($name, PATHINFO_FILENAME);
            $extension = pathinfo($name, PATHINFO_EXTENSION);
            $newFileName=$filename . '_'.$uid .".". $extension; 
            $path = Storage::putFileAs('public/files',$file, $newFileName);

            return response()->json([
                "success" => true,
                "message" => "File successfully uploaded",
                "file" =>$path
            ]);
  
        }
    }
    public function uploadimage(Request $request)
    {
 
       $validator = Validator::make($request->all(),[ 
              'image' => 'required|mimes:jpeg,png,jpg,gif,svg|max:2048',
        ]);   
 
        if($validator->fails()) {          
            
            return response()->json(['error'=>$validator->errors()], 401);                        
         }  
 
        if ($file = $request->file('image')) {

            $name = $file->getClientOriginalName();
            $uid=rand(10000 , 99999);
            $filename = pathinfo($name, PATHINFO_FILENAME);
            $extension = pathinfo($name, PATHINFO_EXTENSION);
            $newFileName=$filename . '_'.$uid .".". $extension; 
            $path = Storage::putFileAs('public/images',$file, $newFileName);

            return response()->json([
                "success" => true,
                "message" => "Image successfully uploaded",
                "file" =>$path
            ]);
  
        }
    }
}